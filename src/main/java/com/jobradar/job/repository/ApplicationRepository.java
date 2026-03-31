package com.jobradar.job.repository;

import com.jobradar.auth.entity.User;
import com.jobradar.job.entity.Application;
import com.jobradar.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudent(User student);
    List<Application> findByJob(Job job);
    Optional<Application> findByJobAndStudent(Job job, User student);
}
