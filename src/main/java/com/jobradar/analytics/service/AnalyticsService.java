package com.jobradar.analytics.service;

import com.jobradar.job.repository.ApplicationRepository;
import com.jobradar.job.repository.JobRepository;
import com.jobradar.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        long jobCount = jobRepository.count();
        long appCount = applicationRepository.count();
        
        stats.put("totalActiveJobs", jobCount); 
        stats.put("totalApplications", appCount);
        stats.put("totalUsers", userRepository.count());
        
        // Comprehensive performance tracking
        double avgConversion = jobRepository.findAll().stream()
                .filter(j -> j.getViewCount() != null && j.getViewCount() > 0)
                .mapToDouble(j -> (double) (j.getApplicationCount() != null ? j.getApplicationCount() : 0) / j.getViewCount())
                .average()
                .orElse(0.0);
        
        stats.put("avgConversionRate", String.format("%.2f%%", avgConversion * 100));

        // Most applied jobs (Top 5)
        stats.put("topJobs", jobRepository.findAll().stream()
                .sorted((j1, j2) -> Long.compare(j2.getApplicationCount() != null ? j2.getApplicationCount() : 0L, 
                                               j1.getApplicationCount() != null ? j1.getApplicationCount() : 0L))
                .limit(5)
                .map(j -> Map.of(
                    "id", j.getId(), 
                    "title", j.getTitle(), 
                    "applications", j.getApplicationCount() != null ? j.getApplicationCount() : 0,
                    "conversion", String.format("%.2f%%", (j.getViewCount() != null && j.getViewCount() > 0) ? 
                        ((double)(j.getApplicationCount() != null ? j.getApplicationCount() : 0) / j.getViewCount() * 100) : 0.0)
                ))
                .collect(java.util.stream.Collectors.toList()));

        return stats;
    }
}
