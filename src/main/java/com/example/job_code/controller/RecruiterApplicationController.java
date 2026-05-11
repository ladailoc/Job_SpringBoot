package com.example.job_code.controller;

import com.example.job_code.model.AppUser;
import com.example.job_code.model.ApplicationStatus;
import com.example.job_code.service.ApplicationService;
import com.example.job_code.service.CurrentUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recruiter")
public class RecruiterApplicationController {

    private final ApplicationService applicationService;
    private final CurrentUserService currentUserService;

    public RecruiterApplicationController(ApplicationService applicationService,
                                          CurrentUserService currentUserService) {
        this.applicationService = applicationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/applications")
    public String viewAllApplications(Model model) {
        AppUser recruiter = currentUserService.getCurrentUser();

        model.addAttribute("applications", applicationService.getApplicationsForRecruiter(recruiter));
        model.addAttribute("statuses", ApplicationStatus.values());

        return "recruiter/applications";
    }

    @GetMapping("/jobs/{jobId}/applications")
    public String viewApplicationsForJob(@PathVariable Long jobId, Model model) {
        AppUser recruiter = currentUserService.getCurrentUser();

        model.addAttribute("applications", applicationService.getApplicationsForJob(jobId, recruiter));
        model.addAttribute("statuses", ApplicationStatus.values());

        return "recruiter/applications";
    }

    @PostMapping("/applications/{applicationId}/status")
    public String updateStatus(@PathVariable Long applicationId,
                               @RequestParam ApplicationStatus status) {
        AppUser recruiter = currentUserService.getCurrentUser();

        applicationService.updateApplicationStatus(applicationId, status, recruiter);

        return "redirect:/recruiter/applications";
    }
}