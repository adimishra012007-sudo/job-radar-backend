package com.jobradar.rating.controller;

import com.jobradar.auth.entity.User;
import com.jobradar.common.dto.ApiResponse;
import com.jobradar.rating.entity.Rating;
import com.jobradar.rating.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/application/{appId}")
    public ResponseEntity<ApiResponse<Rating>> rateUser(
            @AuthenticationPrincipal User user,
            @PathVariable Long appId,
            @RequestParam Integer stars,
            @RequestParam(required = false) String comment
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            "Success: Reputation signal recorded.", 
            ratingService.rateUser(user.getId(), appId, stars, comment)
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Rating>>> getUserRatings(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(
            "Success: Reputation history retrieved.", 
            ratingService.getRatingsForUser(userId)
        ));
    }
}
