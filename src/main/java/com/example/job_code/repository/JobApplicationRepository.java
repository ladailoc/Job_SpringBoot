package com.example.job_code.repository;

import com.example.job_code.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByApplicantIdAndJobPostingId(Long applicantId, Long jobPostingId);

    @Query("""
            SELECT a
            FROM JobApplication a
            JOIN FETCH a.jobPosting j
            LEFT JOIN FETCH j.recruiter
            WHERE a.applicant.id = :applicantId
            ORDER BY a.appliedAt DESC
            """)
    List<JobApplication> findByApplicantIdOrderByAppliedAtDesc(@Param("applicantId") Long applicantId);

    @Query("""
            SELECT a
            FROM JobApplication a
            JOIN FETCH a.jobPosting j
            JOIN FETCH a.applicant applicant
            WHERE j.recruiter.id = :recruiterId
            ORDER BY a.appliedAt DESC
            """)
    List<JobApplication> findByRecruiterIdOrderByAppliedAtDesc(@Param("recruiterId") Long recruiterId);

    @Query("""
            SELECT a
            FROM JobApplication a
            JOIN FETCH a.jobPosting j
            JOIN FETCH a.applicant applicant
            WHERE j.id = :jobId
            AND j.recruiter.id = :recruiterId
            ORDER BY a.appliedAt DESC
            """)
    List<JobApplication> findByJobIdAndRecruiterId(@Param("jobId") Long jobId,
                                                   @Param("recruiterId") Long recruiterId);

    @Query("""
            SELECT a
            FROM JobApplication a
            JOIN FETCH a.jobPosting j
            JOIN FETCH a.applicant applicant
            WHERE a.id = :applicationId
            AND j.recruiter.id = :recruiterId
            """)
    Optional<JobApplication> findByIdAndRecruiterId(@Param("applicationId") Long applicationId,
                                                    @Param("recruiterId") Long recruiterId);
}