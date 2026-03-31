package com.jobradar.job.repository;

import com.jobradar.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query(value = "SELECT *, " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(lat)) * cos(radians(lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(lat)))) AS distance " +
           "FROM jobs " +
           "WHERE status = 'ACTIVE' AND is_active = true AND is_deleted = false AND is_approved = true " +
           "AND (expires_at IS NULL OR expires_at > NOW()) " +
           "HAVING distance < :radius " +
           "ORDER BY distance ASC", 
           countQuery = "SELECT count(*) FROM jobs " +
                        "WHERE status = 'ACTIVE' AND is_active = true AND is_deleted = false AND is_approved = true " +
                        "AND (expires_at IS NULL OR expires_at > NOW())",
           nativeQuery = true)
    org.springframework.data.domain.Page<Job> findNearbyJobs(@Param("lat") Double lat, @Param("lng") Double lng, @Param("radius") Double radius, org.springframework.data.domain.Pageable pageable);

    @Query(value = "SELECT *, " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(lat)) * cos(radians(lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(lat)))) AS distance " +
           "FROM jobs " +
           "WHERE status = 'ACTIVE' AND is_active = true AND is_deleted = false AND is_approved = true " +
           "AND (expires_at IS NULL OR expires_at > NOW()) " +
           "ORDER BY distance ASC", 
           countQuery = "SELECT count(*) FROM jobs " +
                        "WHERE status = 'ACTIVE' AND is_active = true AND is_deleted = false AND is_approved = true " +
                        "AND (expires_at IS NULL OR expires_at > NOW())",
           nativeQuery = true)
    org.springframework.data.domain.Page<Job> findAllByDistance(@Param("lat") Double lat, @Param("lng") Double lng, org.springframework.data.domain.Pageable pageable);
}
