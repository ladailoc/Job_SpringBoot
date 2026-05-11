package com.example.job_code.controller;

import com.example.job_code.dto.ApplicationForm;
import com.example.job_code.model.AppUser;
import com.example.job_code.model.JobPosting;
import com.example.job_code.service.ApplicationService;
import com.example.job_code.service.CurrentUserService;
import com.example.job_code.service.JobService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ApplicantController {

    private final ApplicationService applicationService;
    private final JobService jobService;
    private final CurrentUserService currentUserService;

    public ApplicantController(ApplicationService applicationService,
                               JobService jobService,
                               CurrentUserService currentUserService) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/jobs/{id}/apply")
    public String showApplyForm(@PathVariable Long id, Model model) {
        JobPosting job = jobService.getJobById(id);

        model.addAttribute("job", job);
        model.addAttribute("applicationForm", new ApplicationForm());

        return "jobs/apply";
    }

    @PostMapping("/jobs/{id}/apply")
    public String applyForJob(@PathVariable Long id,
                              @Valid @ModelAttribute("applicationForm") ApplicationForm applicationForm,
                              BindingResult bindingResult,
                              Model model) {
        JobPosting job = jobService.getJobById(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("job", job);
            return "jobs/apply";
        }

        try {
            AppUser applicant = currentUserService.getCurrentUser();
            applicationService.applyForJob(id, applicationForm, applicant);
        } catch (RuntimeException ex) {
            bindingResult.reject("applyError", ex.getMessage());
            model.addAttribute("job", job);
            return "jobs/apply";
        }

        return "redirect:/applicant/applications?success=true";
    }

    @GetMapping("/applicant/applications")
    public String applicationHistory(Model model) {
        AppUser applicant = currentUserService.getCurrentUser();

        model.addAttribute("applications", applicationService.getApplicationHistory(applicant));
        model.addAttribute("currentUser", applicant);

        return "applicant/history";
    }
}