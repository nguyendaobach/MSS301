package com.mss301.identity_service.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class UserPrinciple implements UserDetails {
    private User user;
    private String roleCode;  // Lưu trữ mã Role để tránh truy cập lazy entity

    public UserPrinciple(User user) {
        this.user = user;
        // Lấy và lưu mã Role khi khởi tạo, khi Hibernate session còn mở
        if (user.getRole() != null) {
            this.roleCode = user.getRole().getCode();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Sử dụng roleCode đã lưu trữ thay vì truy cập lại user.getRole().getCode()
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleCode));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return user.getStatus() == Status.ACTIVE;
    }
}
