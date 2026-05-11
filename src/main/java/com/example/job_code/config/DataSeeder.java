package com.example.job_code.config;

import com.example.job_code.model.AppUser;
import com.example.job_code.model.JobPosting;
import com.example.job_code.model.JobType;
import com.example.job_code.model.Role;
import com.example.job_code.repository.JobPostingRepository;
import com.example.job_code.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               JobPostingRepository jobPostingRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {

            AppUser recruiter = userRepository.findByEmail("recruiter@demo.com")
                    .orElseGet(() -> {
                        AppUser newRecruiter = new AppUser();
                        newRecruiter.setFullName("Demo Recruiter");
                        newRecruiter.setEmail("recruiter@demo.com");
                        newRecruiter.setRole(Role.RECRUITER);
                        newRecruiter.setCompanyName("Demo Company");
                        return newRecruiter;
                    });

            if (recruiter.getPassword() == null || !recruiter.getPassword().startsWith("$2")) {
                recruiter.setPassword(passwordEncoder.encode("123456"));
            }

            userRepository.save(recruiter);

            AppUser applicant = userRepository.findByEmail("applicant@demo.com")
                    .orElseGet(() -> {
                        AppUser newApplicant = new AppUser();
                        newApplicant.setFullName("Demo Applicant");
                        newApplicant.setEmail("applicant@demo.com");
                        newApplicant.setRole(Role.APPLICANT);
                        return newApplicant;
                    });

            if (applicant.getPassword() == null || !applicant.getPassword().startsWith("$2")) {
                applicant.setPassword(passwordEncoder.encode("123456"));
            }

            userRepository.save(applicant);

            if (jobPostingRepository.count() == 0) {
                JobPosting job1 = new JobPosting();
                job1.setTitle("Java Backend Developer");
                job1.setDescription("Phát triển REST API bằng Spring Boot, Spring Data JPA và MySQL.");
                job1.setLocation("Đà Nẵng");
                job1.setSalaryMin(new BigDecimal("1000"));
                job1.setSalaryMax(new BigDecimal("1800"));
                job1.setJobType(JobType.FULL_TIME);
                job1.setActive(true);
                job1.setRecruiter(recruiter);

                JobPosting job2 = new JobPosting();
                job2.setTitle("Frontend Developer Intern");
                job2.setDescription("Tham gia phát triển giao diện web với HTML, CSS, JavaScript và Thymeleaf.");
                job2.setLocation("Hồ Chí Minh");
                job2.setSalaryMin(new BigDecimal("300"));
                job2.setSalaryMax(new BigDecimal("500"));
                job2.setJobType(JobType.INTERNSHIP);
                job2.setActive(true);
                job2.setRecruiter(recruiter);

                jobPostingRepository.save(job1);
                jobPostingRepository.save(job2);
            }
        };
    }
}
