package com.jobradar.rating.repository;

import com.jobradar.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRateeIdOrderByCreatedAtDesc(Long rateeId);
    
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.rateeId = :userId")
    Double getAverageRating(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.rateeId = :userId")
    Long getTotalRatings(@Param("userId") Long userId);
}
