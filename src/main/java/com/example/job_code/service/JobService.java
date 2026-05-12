package com.example.job_code.service;

import com.example.job_code.dto.JobPostingForm;
import com.example.job_code.dto.JobSearchCriteria;
import com.example.job_code.model.AppUser;
import com.example.job_code.model.JobPosting;
import com.example.job_code.repository.JobPostingRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
    public Page<JobPosting> searchJobs(JobSearchCriteria criteria, Pageable pageable) {
        return jobPostingRepository.findAll(buildSearchSpec(criteria), pageable);
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
        validateSalary(form);

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
        validateSalary(form);

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

    private Specification<JobPosting> buildSearchSpec(JobSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("active")));

            if (criteria != null) {
                if (StringUtils.hasText(criteria.getKeyword())) {
                    String keyword = "%" + criteria.getKeyword().trim().toLowerCase() + "%";
                    Predicate titleLike = cb.like(cb.lower(root.get("title")), keyword);
                    Predicate descLike = cb.like(cb.lower(root.get("description")), keyword);
                    predicates.add(cb.or(titleLike, descLike));
                }

                if (StringUtils.hasText(criteria.getLocation())) {
                    String location = "%" + criteria.getLocation().trim().toLowerCase() + "%";
                    predicates.add(cb.like(cb.lower(root.get("location")), location));
                }

                if (criteria.getJobType() != null) {
                    predicates.add(cb.equal(root.get("jobType"), criteria.getJobType()));
                }

                if (criteria.getMinSalary() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("salaryMax"), criteria.getMinSalary()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private JobPosting getJobOwnedByRecruiter(Long jobId, AppUser recruiter) {
        JobPosting job = getJobById(jobId);

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("Bạn không có quyền thao tác công việc này");
        }

        return job;
    }

    private void validateSalary(JobPostingForm form) {
        if (form.getSalaryMin() != null
                && form.getSalaryMax() != null
                && form.getSalaryMin().compareTo(form.getSalaryMax()) > 0) {
            throw new RuntimeException("Lương tối thiểu không được lớn hơn lương tối đa");
        }
    }
}
