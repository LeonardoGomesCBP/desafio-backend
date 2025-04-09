package com.simplesdental.product.service;

import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Logger logger = LoggingService.getLogger(CustomUserDetailsService.class);

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        LoggingService.logWithField(logger, "WARN", "Authentication failure - user not found", "email", email);
                        return new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
                    });

            LoggingService.logWithFields(logger, "INFO", "User authenticated",
                    Map.of(
                            "email", email,
                            "role", user.getRole()
                    ));

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
            );
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LoggingService.logError(logger, "authentication", e);
            throw e;
        }
    }
}