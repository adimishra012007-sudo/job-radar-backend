package com.jobradar.job.controller;

import com.jobradar.auth.entity.User;
import com.jobradar.job.entity.Job;
import com.jobradar.job.service.JobService;
import com.jobradar.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<Job>>> getNearbyJobs(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) Double radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.util.Streamable.of(org.springframework.data.domain.PageRequest.of(page, size)).get().findFirst().get(); // Simplified for now
        // Wait, just use PageRequest.of(page, size)
        pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Success: Signals intercepted.", jobService.findJobsNearMe(lat, lng, skills, radius, pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Job>> createJob(@RequestBody Job job) {
        Job savedJob = jobService.createJob(job);
        return ResponseEntity.ok(ApiResponse.success("Success: Signal initialized. Pending admin moderation.", savedJob));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Job>> approveJob(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success: Signal verified and authorized.", jobService.approveJob(id)));
    }

    @PutMapping("/{id}/urgent")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Job>> toggleUrgent(@PathVariable Long id, @RequestParam boolean isUrgent) {
        Job job = jobService.getJobById(id);
        job.setUrgent(isUrgent);
        return ResponseEntity.ok(ApiResponse.success("Success: Urgency signal updated.", jobService.saveJob(job)));
    }

    @PutMapping("/{id}/boost")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Job>> toggleBoost(@PathVariable Long id, @RequestParam boolean isBoosted) {
        Job job = jobService.getJobById(id);
        job.setBoosted(isBoosted);
        return ResponseEntity.ok(ApiResponse.success("Success: Signal amplification updated.", jobService.saveJob(job)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> getJobById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        if (user != null) {
            jobService.logView(user.getId(), id);
        }
        return ResponseEntity.ok(ApiResponse.success("Success: Signal retrieved and decoded.", jobService.getJobById(id)));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<java.util.List<Job>>> getRecentJobs(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Success: Recent signals retrieved.", jobService.getRecentJobs(user.getId())));
    }

    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<Job>>> getRecommendedJobs(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Success: Personalized signals mapped.", jobService.getRecommendedJobs(user, pageable)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        jobService.softDeleteJob(id);
        return ResponseEntity.ok(ApiResponse.success("Success: Signal neutralized.", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Job>>> getAllJobs() {
        return ResponseEntity.ok(ApiResponse.success("Success: Global signal feed.", jobService.getAllJobs()));
    }
}
