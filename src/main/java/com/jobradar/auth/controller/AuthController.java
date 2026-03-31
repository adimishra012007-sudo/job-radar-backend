package com.jobradar.auth.controller;

import com.jobradar.auth.dto.AuthRequest;
import com.jobradar.auth.dto.AuthResponse;
import com.jobradar.auth.service.AuthService;
import com.jobradar.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Success: Operative registered.", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Success: Security cleared.", authService.login(request)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success("Success: Signal refreshed.", authService.refreshToken(refreshToken)));
    }
}
