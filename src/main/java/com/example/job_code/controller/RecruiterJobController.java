package com.example.job_code.controller;

import com.example.job_code.dto.JobPostingForm;
import com.example.job_code.model.AppUser;
import com.example.job_code.model.JobType;

import com.example.job_code.service.CurrentUserService;
import com.example.job_code.service.JobService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recruiter/jobs")
public class RecruiterJobController {

    private final JobService jobService;
    private final CurrentUserService currentUserService;

    public RecruiterJobController(JobService jobService,
                                  CurrentUserService currentUserService) {
        this.jobService = jobService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String listJobs(Model model) {
        AppUser recruiter = currentUserService.getCurrentUser();

        model.addAttribute("jobs", jobService.getJobsOfRecruiter(recruiter));
        model.addAttribute("currentUser", recruiter);

        return "recruiter/jobs";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("jobForm", new JobPostingForm());
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("pageTitle", "Đăng công việc mới");
        model.addAttribute("formAction", "/recruiter/jobs");

        return "recruiter/job-form";
    }

    @PostMapping
    public String createJob(@Valid @ModelAttribute("jobForm") JobPostingForm jobForm,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobTypes", JobType.values());
            model.addAttribute("pageTitle", "Đăng công việc mới");
            model.addAttribute("formAction", "/recruiter/jobs");
            return "recruiter/job-form";
        }

        AppUser recruiter = currentUserService.getCurrentUser();
        jobService.createJob(jobForm, recruiter);

        return "redirect:/recruiter/jobs";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        AppUser recruiter = currentUserService.getCurrentUser();
        JobPostingForm jobForm = jobService.getFormForEdit(id, recruiter);

        model.addAttribute("jobForm", jobForm);
        model.addAttribute("jobTypes", JobType.values());
        model.addAttribute("pageTitle", "Cập nhật công việc");
        model.addAttribute("formAction", "/recruiter/jobs/" + id + "/edit");

        return "recruiter/job-form";
    }

    @PostMapping("/{id}/edit")
    public String updateJob(@PathVariable Long id,
                            @Valid @ModelAttribute("jobForm") JobPostingForm jobForm,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobTypes", JobType.values());
            model.addAttribute("pageTitle", "Cập nhật công việc");
            model.addAttribute("formAction", "/recruiter/jobs/" + id + "/edit");
            return "recruiter/job-form";
        }

        AppUser recruiter = currentUserService.getCurrentUser();
        jobService.updateJob(id, jobForm, recruiter);

        return "redirect:/recruiter/jobs";
    }

    @PostMapping("/{id}/delete")
    public String deleteJob(@PathVariable Long id) {
        AppUser recruiter = currentUserService.getCurrentUser();
        jobService.deleteJob(id, recruiter);

        return "redirect:/recruiter/jobs";
    }
}