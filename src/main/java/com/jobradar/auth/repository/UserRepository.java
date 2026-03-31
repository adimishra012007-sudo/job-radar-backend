package com.jobradar.auth.repository;

import com.jobradar.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM users " +
            "WHERE role = 'STUDENT' AND lat IS NOT NULL AND lng IS NOT NULL " +
            "AND (6371 * acos(cos(radians(:lat)) * cos(radians(lat)) * cos(radians(lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(lat)))) < :radius",
            nativeQuery = true)
    java.util.List<User> findNearbyStudents(@org.springframework.data.repository.query.Param("lat") Double lat, 
                                            @org.springframework.data.repository.query.Param("lng") Double lng, 
                                            @org.springframework.data.repository.query.Param("radius") Double radius);
}

// Separate file for RefreshTokenRepository
/*
package com.jobradar.auth.repository;

import com.jobradar.auth.entity.RefreshToken;
import com.jobradar.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
*/
