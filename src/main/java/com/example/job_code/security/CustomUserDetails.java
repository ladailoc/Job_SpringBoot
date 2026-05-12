package com.example.job_code.security;

import com.example.job_code.model.AppUser;
import com.example.job_code.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String fullName;
    private final String email;
    private final String password;
    private final Role role;
    private final String companyName;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(AppUser appUser) {
        this.id = appUser.getId();
        this.fullName = appUser.getFullName();
        this.email = appUser.getEmail();
        this.password = appUser.getPassword();
        this.role = appUser.getRole();
        this.companyName = appUser.getCompanyName();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole().name()));
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRoleName() {
        if (role == Role.RECRUITER) {
            return "Nhà tuyển dụng";
        }
        return "Ứng viên";
    }

    public String getInitial() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName.substring(0, 1).toUpperCase();
        }
        return "?";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
