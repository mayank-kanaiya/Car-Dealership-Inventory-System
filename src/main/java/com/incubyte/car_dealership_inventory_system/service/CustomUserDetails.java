package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapts the application's {@link User} entity to Spring Security's
 * {@link UserDetails} contract. The role is prefixed with {@code "ROLE_"}
 * so that Spring Security's role-based authorization annotations and
 * configuration (e.g. {@code @PreAuthorize("hasRole('ADMIN')")}) work
 * out of the box.
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Returns the user's role prefixed with {@code "ROLE_"}, which is the
     * convention expected by Spring Security's {@code hasRole()} expressions.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Maps to the user's email, which also serves as the login username.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
