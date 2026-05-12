package com.example.job_code.controller.api;

import com.example.job_code.dto.JobResponse;
import com.example.job_code.dto.JobSearchCriteria;
import com.example.job_code.model.JobPosting;
import com.example.job_code.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobApiController {

    private final JobService jobService;

    public JobApiController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public Page<JobResponse> searchJobs(JobSearchCriteria criteria,
                                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                        Pageable pageable) {
        return jobService.searchJobs(criteria, pageable)
                .map(JobResponse::from);
    }

    @GetMapping("/{id}")
    public JobResponse getJobDetail(@PathVariable Long id) {
        JobPosting job = jobService.getJobById(id);
        return JobResponse.from(job);
    }
}
