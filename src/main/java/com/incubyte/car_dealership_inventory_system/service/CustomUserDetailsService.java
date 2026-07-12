package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridges Spring Security's {@link UserDetailsService} contract with our
 * {@link UserRepository}, looking up users by email address and adapting
 * them to {@link UserDetails} via {@link CustomUserDetails}.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Locates the user by their email address and wraps the result in a
     * {@link CustomUserDetails} instance suitable for Spring Security's
     * authentication provider chain.
     *
     * @param username the email address used as the login identifier
     * @return the user's security context
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}
