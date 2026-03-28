package com.capfer.hotel.booking.security;

import com.capfer.hotel.booking.entities.User;
import com.capfer.hotel.booking.exceptions.NotFoundException;
import com.capfer.hotel.booking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + username));

        log.info("User found: {}", user.getEmail());
        log.info("User role: {}", user.getRole());

        return AuthUser.builder()
                .user(user)
                .build();
    }
}
