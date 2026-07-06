package com.project.ems.security.service;

import com.project.ems.security.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 *
 * This class acts as an adapter between our User entity
 * and Spring Security.
 *
 * Spring Security never works directly with our User entity.
 * Instead, it authenticates and authorizes using this class.
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    /**
     * The actual User entity fetched from the database.
     */
    private final User user;

    /**
     * Returns the user's authorities (roles/permissions).
     *
     * Spring Security uses these authorities during
     * authorization checks.
     *
     * Example:
     * ROLE_ADMIN
     * ROLE_HR
     * ROLE_EMPLOYEE
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().getName().name()
                )
        );
    }

    /**
     * Returns the encrypted password.
     *
     * During login Spring Security compares
     * the entered password with this BCrypt hash.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used for login.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the account has expired.
     *
     * Returning true means the account is valid.
     *
     * Later we can connect this with the database
     * if business requirements demand it.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account is locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked();
    }

    /**
     * Indicates whether the user's credentials
     * (password) have expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account is enabled.
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    /**
     * Returns the wrapped User entity.
     *
     * Useful whenever we need additional information
     * about the authenticated user.
     */
    public User getUser() {
        return user;
    }
}