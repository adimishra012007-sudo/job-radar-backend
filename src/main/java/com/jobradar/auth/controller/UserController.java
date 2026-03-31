package com.jobradar.auth.controller;

import com.jobradar.auth.entity.User;
import com.jobradar.auth.repository.UserRepository;
import com.jobradar.common.dto.ApiResponse;
import com.jobradar.common.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Success: Current user profile.", user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody User profileUpdates
    ) {
        if (profileUpdates.getSkills() != null) user.setSkills(profileUpdates.getSkills());
        if (profileUpdates.getLat() != null) user.setLat(profileUpdates.getLat());
        if (profileUpdates.getLng() != null) user.setLng(profileUpdates.getLng());
        if (profileUpdates.getPreferredJobType() != null) user.setPreferredJobType(profileUpdates.getPreferredJobType());
        if (profileUpdates.getAvailability() != null) user.setAvailability(profileUpdates.getAvailability());
        if (profileUpdates.getLocationPreference() != null) user.setLocationPreference(profileUpdates.getLocationPreference());
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Success: Profile updated.", savedUser));
    }

    @PostMapping("/resume")
    public ResponseEntity<ApiResponse<User>> uploadResume(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file
    ) {
        return upload(user, file);
    }

    @PostMapping("/uploadResume")
    public ResponseEntity<ApiResponse<User>> uploadResumeAlias(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file
    ) {
        return upload(user, file);
    }

    private ResponseEntity<ApiResponse<User>> upload(User user, MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        
        user.setResumeUrl("/uploads/resumes/" + fileName);
        user.setResumeName(file.getOriginalFilename());
        
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Success: Resume uploaded.", savedUser));
    }
}
