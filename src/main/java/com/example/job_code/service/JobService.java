package com.example.job_code.service;

import com.example.job_code.dto.JobPostingForm;
import com.example.job_code.model.AppUser;
import com.example.job_code.model.JobPosting;
import com.example.job_code.repository.JobPostingRepository;
import com.example.job_code.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobService {

    private final JobPostingRepository jobPostingRepository;

    public JobService(JobPostingRepository jobPostingRepository) {
        this.jobPostingRepository = jobPostingRepository;
    }

    @Transactional(readOnly = true)
    public List<JobPosting> getAllActiveJobs() {
        return jobPostingRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public JobPosting getJobById(Long id) {
        return jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc với id: " + id));
    }

    @Transactional(readOnly = true)
    public List<JobPosting> getJobsOfRecruiter(AppUser recruiter) {
        return jobPostingRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiter.getId());
    }

    @Transactional
    public void createJob(JobPostingForm form, AppUser recruiter) {
        JobPosting job = new JobPosting();
        job.setTitle(form.getTitle());
        job.setDescription(form.getDescription());
        job.setLocation(form.getLocation());
        job.setSalaryMin(form.getSalaryMin());
        job.setSalaryMax(form.getSalaryMax());
        job.setJobType(form.getJobType());
        job.setActive(form.isActive());
        job.setRecruiter(recruiter);

        jobPostingRepository.save(job);
    }

    @Transactional(readOnly = true)
    public JobPostingForm getFormForEdit(Long jobId, AppUser recruiter) {
        JobPosting job = getJobOwnedByRecruiter(jobId, recruiter);

        JobPostingForm form = new JobPostingForm();
        form.setTitle(job.getTitle());
        form.setDescription(job.getDescription());
        form.setLocation(job.getLocation());
        form.setSalaryMin(job.getSalaryMin());
        form.setSalaryMax(job.getSalaryMax());
        form.setJobType(job.getJobType());
        form.setActive(job.isActive());

        return form;
    }

    @Transactional
    public void updateJob(Long jobId, JobPostingForm form, AppUser recruiter) {
        JobPosting job = getJobOwnedByRecruiter(jobId, recruiter);

        job.setTitle(form.getTitle());
        job.setDescription(form.getDescription());
        job.setLocation(form.getLocation());
        job.setSalaryMin(form.getSalaryMin());
        job.setSalaryMax(form.getSalaryMax());
        job.setJobType(form.getJobType());
        job.setActive(form.isActive());

        jobPostingRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long jobId, AppUser recruiter) {
        JobPosting job = getJobOwnedByRecruiter(jobId, recruiter);
        jobPostingRepository.delete(job);
    }

    private JobPosting getJobOwnedByRecruiter(Long jobId, AppUser recruiter) {
        JobPosting job = getJobById(jobId);

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("Bạn không có quyền thao tác công việc này");
        }

        return job;
    }
}