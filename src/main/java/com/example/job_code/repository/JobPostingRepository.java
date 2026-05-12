package com.example.job_code.repository;

import com.example.job_code.model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long>,
        JpaSpecificationExecutor<JobPosting> {

    List<JobPosting> findByActiveTrueOrderByCreatedAtDesc();

    List<JobPosting> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);
}