package com.jobradar.job.repository;

import com.jobradar.job.entity.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {
    List<ViewLog> findByUserIdOrderByViewDateDesc(Long userId);
    void deleteByUserIdAndJobId(Long userId, Long jobId);
}
