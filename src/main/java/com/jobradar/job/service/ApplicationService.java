package com.jobradar.job.service;

import com.jobradar.auth.entity.User;
import com.jobradar.job.entity.Application;
import com.jobradar.job.entity.Job;
import com.jobradar.job.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobService jobService;
    private final com.jobradar.notification.service.NotificationService notificationService;

    @Transactional
    public Application applyToJob(User user, Long jobId) {
        Job job = jobService.getJobById(jobId);
        
        // Check if already applied
        if (applicationRepository.findByJobAndStudent(job, user).isPresent()) {
            throw new RuntimeException("Protocol error: Identity already engaged with this signal.");
        }

        Application application = Application.builder()
                .job(job)
                .student(user)
                .appliedAt(LocalDateTime.now())
                .resumeUrl(user.getResumeUrl())
                .resumeName(user.getResumeName())
                .status(Application.ApplicationStatus.APPLIED)
                .build();

        Application saved = applicationRepository.save(application);
        if (saved == null) {
            throw new RuntimeException("Failed to save application signal.");
        }
        jobService.incrementApplicationCount(jobId);
        return saved;
    }

    public List<Application> getUserApplications(User user) {
        return applicationRepository.findByStudent(user);
    }

    public List<Application> getJobApplications(Long jobId) {
        Job job = jobService.getJobById(jobId);
        return applicationRepository.findByJob(job);
    }

    public Application updateApplicationStatus(Long id, Application.ApplicationStatus status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application signal not found."));
        application.setStatus(status);
        Application saved = applicationRepository.save(application);

        // Notify student
        notificationService.sendNotification(
            application.getStudent().getId(),
            "Protocol Update: Your application for " + application.getJob().getTitle() + " is now " + status.name(),
            com.jobradar.notification.entity.Notification.NotificationType.APP_STATUS
        );

        return saved;
    }
}
