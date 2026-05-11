package com.example.job_code.controller;

import com.example.job_code.dto.RegisterForm;
import com.example.job_code.model.Role;
import com.example.job_code.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        model.addAttribute("roles", Role.values());

        return "auth/signup";
    }

    @PostMapping("/signup")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "auth/signup";
        }

        try {
            authService.register(registerForm);
        } catch (RuntimeException ex) {
            bindingResult.reject("registerError", ex.getMessage());
            model.addAttribute("roles", Role.values());
            return "auth/signup";
        }

        return "redirect:/auth/login?registered=true";
    }
}
