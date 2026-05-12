package com.example.job_code.service;

import com.example.job_code.dto.ApplicationForm;
import com.example.job_code.model.AppUser;
import com.example.job_code.model.ApplicationStatus;
import com.example.job_code.model.JobApplication;
import com.example.job_code.model.JobPosting;
import com.example.job_code.repository.JobApplicationRepository;
import com.example.job_code.repository.JobPostingRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final NotificationService notificationService;

    public ApplicationService(JobApplicationRepository applicationRepository,
                              JobPostingRepository jobPostingRepository,
                              NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void applyForJob(Long jobId, ApplicationForm form, AppUser applicant) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc"));

        if (!job.isActive()) {
            throw new RuntimeException("Công việc này đã đóng tuyển dụng");
        }

        boolean alreadyApplied = applicationRepository
                .existsByApplicantIdAndJobPostingId(applicant.getId(), jobId);

        if (alreadyApplied) {
            throw new RuntimeException("Bạn đã ứng tuyển công việc này rồi");
        }

        JobApplication application = new JobApplication();
        application.setJobPosting(job);
        application.setApplicant(applicant);
        application.setCoverLetter(form.getCoverLetter());
        application.setCvLink(form.getCvLink());
        application.setStatus(ApplicationStatus.NEW);

        try {
            applicationRepository.saveAndFlush(application);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Bạn đã ứng tuyển công việc này rồi");
        }

        // Notify recruiter about new application
        String message = applicant.getFullName() + " đã ứng tuyển vào vị trí \"" + job.getTitle() + "\"";
        String link = "/recruiter/applications";
        notificationService.createNotification(job.getRecruiter(), message, link);
    }

    @Transactional(readOnly = true)
    public List<Long> getAppliedJobIds(Long applicantId) {
        return applicationRepository.findByApplicantIdOrderByAppliedAtDesc(applicantId)
                .stream()
                .map(a -> a.getJobPosting().getId())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationHistory(AppUser applicant) {
        return applicationRepository.findByApplicantIdOrderByAppliedAtDesc(applicant.getId());
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsForRecruiter(AppUser recruiter) {
        return applicationRepository.findByRecruiterIdOrderByAppliedAtDesc(recruiter.getId());
    }

    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsForJob(Long jobId, AppUser recruiter) {
        return applicationRepository.findByJobIdAndRecruiterId(jobId, recruiter.getId());
    }

    @Transactional
    public void updateApplicationStatus(Long applicationId,
                                        ApplicationStatus status,
                                        AppUser recruiter) {
        JobApplication application = applicationRepository
                .findByIdAndRecruiterId(applicationId, recruiter.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn ứng tuyển hoặc bạn không có quyền cập nhật"));

        application.setStatus(status);
        applicationRepository.save(application);

        // Notify applicant about status change
        String statusText = switch (status) {
            case REVIEWING -> "đang được xem xét";
            case INTERVIEWED -> "đã được mời phỏng vấn";
            case ACCEPTED -> "đã được chấp nhận";
            case REJECTED -> "đã bị từ chối";
            default -> "đã được cập nhật";
        };
        String message = "Đơn ứng tuyển vào vị trí \"" + application.getJobPosting().getTitle()
                + "\" " + statusText;
        String link = "/applicant/applications";
        notificationService.createNotification(application.getApplicant(), message, link);
    }
}