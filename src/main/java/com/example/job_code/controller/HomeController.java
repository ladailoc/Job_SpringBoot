package com.example.job_code.controller;

import com.example.job_code.dto.JobSearchCriteria;
import com.example.job_code.model.JobPosting;
import com.example.job_code.model.JobType;
import com.example.job_code.service.ApplicationService;
import com.example.job_code.service.CurrentUserService;
import com.example.job_code.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;

@Controller
public class HomeController {
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final CurrentUserService currentUserService;

    public HomeController(JobService jobService,
                          ApplicationService applicationService,
                          CurrentUserService currentUserService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.currentUserService = currentUserService;
    }

    private void addAppliedJobIds(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_APPLICANT"))) {
            try {
                Long userId = currentUserService.getCurrentUser().getId();
                model.addAttribute("appliedJobIds", applicationService.getAppliedJobIds(userId));
            } catch (Exception e) {
                model.addAttribute("appliedJobIds", Collections.emptyList());
            }
        } else {
            model.addAttribute("appliedJobIds", Collections.emptyList());
        }
    }

    @GetMapping("/")
    public String home(JobSearchCriteria criteria,
                       @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {

        Page<JobPosting> jobPage = jobService.searchJobs(criteria, pageable);

        model.addAttribute("jobPage", jobPage);
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("criteria", criteria);
        model.addAttribute("jobTypes", JobType.values());
        addAppliedJobIds(model);

        return "home";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        JobPosting job = jobService.getJobById(id);

        model.addAttribute("job", job);
        addAppliedJobIds(model);

        return "jobs/detail";
    }
}
