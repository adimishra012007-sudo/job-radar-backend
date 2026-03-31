package com.jobradar.job.controller;

import com.jobradar.auth.entity.User;
import com.jobradar.common.dto.ApiResponse;
import com.jobradar.job.entity.Application;
import com.jobradar.job.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Application>> applyToJob(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Long> request
    ) {
        return apply(user, request);
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<Application>> applyToJobAlias(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Long> request
    ) {
        return apply(user, request);
    }

    private ResponseEntity<ApiResponse<Application>> apply(User user, Map<String, Long> request) {
        Long jobId = request.get("jobId");
        return ResponseEntity.ok(ApiResponse.success(
                "Success: Application signal launched and intercepted.", 
                applicationService.applyToJob(user, jobId)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<Application>>> getMyApplications(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Success: Your engagement signals retrieved.", 
                applicationService.getUserApplications(user)
        ));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<List<Application>>> getJobApplications(
            @PathVariable Long jobId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Success: Job engagement signals decoded.", 
                applicationService.getJobApplications(jobId)
        ));
    }

    @PutMapping("/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Application>> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam Application.ApplicationStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Success: Application status updated.", 
                applicationService.updateApplicationStatus(id, status)
        ));
    }
}
