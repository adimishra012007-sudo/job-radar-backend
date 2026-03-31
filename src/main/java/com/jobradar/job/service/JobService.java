package com.jobradar.job.service;

import com.jobradar.job.entity.Job;
import com.jobradar.job.entity.ViewLog;
import com.jobradar.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final com.jobradar.job.repository.ViewLogRepository viewLogRepository;
    private final com.jobradar.auth.repository.UserRepository userRepository;
    private final com.jobradar.notification.service.NotificationService notificationService;
    private static final double DEFAULT_RADIUS_KM = 10.0;
    private static final double ALERT_RADIUS_KM = 20.0;

    public org.springframework.data.domain.Page<Job> findJobsNearMe(Double lat, Double lng, String userSkills, Double radius, org.springframework.data.domain.Pageable pageable) {
        double searchRadius = (radius != null) ? radius : DEFAULT_RADIUS_KM;
        
        // 1. Strict radius filtering
        org.springframework.data.domain.Page<Job> jobsPage = jobRepository.findNearbyJobs(lat, lng, searchRadius, pageable);
        
        // 2. Hybrid Expansion: If first page has very few results, fallback to distance-sorted global search
        if (jobsPage.getTotalElements() < 3 && pageable.getPageNumber() == 0) {
            jobsPage = jobRepository.findAllByDistance(lat, lng, pageable);
        }

        // 3. Rank results using MatchingEngine
        if (userSkills != null && !userSkills.isEmpty()) {
            List<Job> sortedJobs = new java.util.ArrayList<>(jobsPage.getContent());
            sortedJobs.sort((j1, j2) -> Double.compare(
                com.jobradar.matching.MatchingEngine.calculateScore(j2, lat, lng, userSkills),
                com.jobradar.matching.MatchingEngine.calculateScore(j1, lat, lng, userSkills)
            ));
            return new org.springframework.data.domain.PageImpl<>(sortedJobs, pageable, jobsPage.getTotalElements());
        }
        
        return jobsPage;
    }

    public Job approveJob(Long id) {
        Job job = getJobById(id);
        job.setApproved(true);
        Job saved = jobRepository.save(job);

        // Notify nearby students
        java.util.List<com.jobradar.auth.entity.User> nearbyStudents = userRepository.findNearbyStudents(job.getLat(), job.getLng(), ALERT_RADIUS_KM);
        for (com.jobradar.auth.entity.User student : nearbyStudents) {
            notificationService.sendNotification(
                student.getId(),
                "New Signal: " + job.getTitle() + " at " + job.getCompany() + " near you!",
                com.jobradar.notification.entity.Notification.NotificationType.JOB_ALERT
            );
        }
        return saved;
    }

    public Job markAsFastHiring(Long id, boolean enabled) {
        Job job = getJobById(id);
        job.setFastHiring(enabled);
        if (enabled) {
            job.setFastHiringAt(java.time.LocalDateTime.now());
        }
        return jobRepository.save(job);
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public Job createJob(Job job) {
        job.setCreatedAt(java.time.LocalDateTime.now());
        if (job.getExpiresAt() == null) {
            job.setExpiresAt(job.getCreatedAt().plusDays(30));
        }
        job.setStatus(Job.JobStatus.ACTIVE);
        job.setActive(true);
        job.setDeleted(false);
        job.setViewCount(0L);
        job.setApplicationCount(0L);
        return jobRepository.save(job);
    }

    public Job getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        // Increment view count
        Long currentViews = job.getViewCount();
        job.setViewCount(currentViews != null ? currentViews + 1 : 1L);
        return jobRepository.save(job);
    }

    @org.springframework.transaction.annotation.Transactional
    public void logView(Long userId, Long jobId) {
        viewLogRepository.deleteByUserIdAndJobId(userId, jobId);
        ViewLog log = ViewLog.builder()
                .userId(userId)
                .jobId(jobId)
                .viewDate(java.time.LocalDateTime.now())
                .build();
        viewLogRepository.save(log);
    }

    public java.util.List<Job> getRecentJobs(Long userId) {
        return viewLogRepository.findByUserIdOrderByViewDateDesc(userId).stream()
                .limit(10)
                .map(log -> jobRepository.findById(log.getJobId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }

    public org.springframework.data.domain.Page<Job> getRecommendedJobs(com.jobradar.auth.entity.User user, org.springframework.data.domain.Pageable pageable) {
        // Simple recommendation: Jobs matching preferred type or skills
        // This can be expanded to complex ML mapping later
        return findJobsNearMe(user.getLat(), user.getLng(), user.getSkills(), 50.0, pageable);
    }

    public void incrementApplicationCount(Long jobId) {
        if (jobId != null) {
            jobRepository.findById(jobId).ifPresent(job -> {
                Long currentApps = job.getApplicationCount();
                job.setApplicationCount(currentApps != null ? currentApps + 1 : 1L);
                jobRepository.save(job);
            });
        }
    }

    public void softDeleteJob(Long id) {
        if (id != null) {
            jobRepository.findById(id).ifPresent(job -> {
                job.setDeleted(true);
                jobRepository.save(job);
            });
        }
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll().stream()
                .filter(j -> !j.isDeleted() && (j.getExpiresAt() == null || j.getExpiresAt().isAfter(java.time.LocalDateTime.now())))
                .collect(java.util.stream.Collectors.toList());
    }
}
