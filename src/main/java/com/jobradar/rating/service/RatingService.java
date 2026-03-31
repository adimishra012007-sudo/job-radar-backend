package com.jobradar.rating.service;

import com.jobradar.auth.entity.User;
import com.jobradar.auth.repository.UserRepository;
import com.jobradar.job.entity.Application;
import com.jobradar.job.repository.ApplicationRepository;
import com.jobradar.rating.entity.Rating;
import com.jobradar.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Rating rateUser(Long raterId, Long applicationId, Integer stars, String comment) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application signal not found."));

        // Rule: Only allow rating if status is SELECTED
        if (application.getStatus() != Application.ApplicationStatus.SELECTED) {
            throw new RuntimeException("Protocol Error: Ratings only authorized for SELECTED engagements.");
        }

        // Determine ratee 
        Long rateeId;
        if (raterId.equals(application.getStudent().getId())) {
            // Student rating employer
            rateeId = application.getJob().getEmployer().getId();
        } else if (raterId.equals(application.getJob().getEmployer().getId())) {
            // Employer rating student
            rateeId = application.getStudent().getId();
        } else {
            throw new RuntimeException("Unauthorized: Signal interaction not recognized.");
        }
        
        Rating rating = Rating.builder()
                .raterId(raterId)
                .rateeId(rateeId)
                .applicationId(applicationId)
                .stars(stars)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();
        
        Rating saved = ratingRepository.save(rating);
        
        // Update User metrics
        updateUserRatingMetrics(rateeId);
        
        return saved;
    }

    private void updateUserRatingMetrics(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Double avg = ratingRepository.getAverageRating(userId);
            Long total = ratingRepository.getTotalRatings(userId);
            user.setAverageRating(avg != null ? avg : 0.0);
            user.setTotalRatings(total != null ? total : 0L);
            userRepository.save(user);
        }
    }
    
    public List<Rating> getRatingsForUser(Long userId) {
        return ratingRepository.findByRateeIdOrderByCreatedAtDesc(userId);
    }
}
