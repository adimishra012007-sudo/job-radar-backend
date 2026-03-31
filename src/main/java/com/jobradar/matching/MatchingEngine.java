package com.jobradar.matching;

import com.jobradar.job.entity.Job;
import lombok.Data;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class MatchingEngine {

    public static double calculateScore(Job job, Double userLat, Double userLng, String userSkills) {
        double distanceScore = calculateDistanceScore(job, userLat, userLng);
        double skillScore = calculateSkillScore(job, userSkills);
        double recencyScore = calculateRecencyScore(job);

        // Weighted Average: 40% Distance, 40% Skills, 20% Recency
        double baseScore = (distanceScore * 0.4) + (skillScore * 0.4) + (recencyScore * 0.2);

        // Add Priority Boosts
        if (job.isBoosted()) baseScore += 30.0;
        if (job.isUrgent()) baseScore += 20.0;
        if (job.isFastHiring()) baseScore += 25.0;

        return baseScore;
    }

    private static double calculateDistanceScore(Job job, Double lat, Double lng) {
        double distance = haversine(lat, lng, job.getLat(), job.getLng());
        if (distance < 2.0) return 100.0;
        if (distance > 100.0) return 0.0;
        return 100.0 * (1.0 - (distance / 100.0));
    }

    private static double calculateSkillScore(Job job, String userSkills) {
        if (userSkills == null || userSkills.isEmpty()) return 0.0;
        Set<String> userSkillSet = Arrays.stream(userSkills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        Set<String> jobSkillSet = Arrays.stream(job.getSkillsRequired().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        long matches = jobSkillSet.stream().filter(userSkillSet::contains).count();
        return (100.0 * matches) / jobSkillSet.size();
    }

    private static double calculateRecencyScore(Job job) {
        long daysOld = Duration.between(job.getCreatedAt(), LocalDateTime.now()).toDays();
        if (daysOld < 1) return 100.0;
        if (daysOld > 30) return 10.0;
        return 100.0 - (daysOld * 3.0);
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
