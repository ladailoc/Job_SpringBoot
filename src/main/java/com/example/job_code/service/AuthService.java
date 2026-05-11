package com.example.job_code.service;

import com.example.job_code.dto.RegisterForm;
import com.example.job_code.model.AppUser;
import com.example.job_code.model.Role;
import com.example.job_code.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        if (form.getRole() == Role.RECRUITER) {
            if (form.getCompanyName() == null || form.getCompanyName().trim().isEmpty()) {
                throw new RuntimeException("Nhà tuyển dụng phải nhập tên công ty");
            }
        }

        AppUser user = new AppUser();
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(form.getRole());
        user.setCompanyName(form.getCompanyName());

        userRepository.save(user);
    }
}
