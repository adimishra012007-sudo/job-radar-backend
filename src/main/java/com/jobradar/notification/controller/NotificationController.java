package com.jobradar.notification.controller;

import com.jobradar.auth.entity.User;
import com.jobradar.common.dto.ApiResponse;
import com.jobradar.notification.entity.Notification;
import com.jobradar.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
            "Success: Alerts retrieved.", 
            notificationService.getUserNotifications(user.getId())
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
            "Success: Unread count calculated.", 
            notificationService.getUnreadCount(user.getId())
        ));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Success: Alert neutralized.", null));
    }
}
