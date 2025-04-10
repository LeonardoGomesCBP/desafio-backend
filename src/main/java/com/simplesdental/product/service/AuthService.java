package com.simplesdental.product.service;

import com.simplesdental.product.dto.AuthDTOs.AuthRequestDTO;
import com.simplesdental.product.dto.AuthDTOs.AuthResponseDTO;
import com.simplesdental.product.dto.AuthDTOs.RegisterRequestDTO;
import com.simplesdental.product.dto.ExceptionResponse;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggingService.getLogger(AuthService.class);

    @Autowired
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponseDTO login(AuthRequestDTO request) {
        String operationId = LoggingService.startOperation(logger, "user_login");
        long startTime = System.currentTimeMillis();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        logger.warn("User not found during login: {}", request.getEmail());
                        return new ExceptionResponse("Usuário não encontrado");
                    });

            UserDetails userDetails = createUserDetails(user);
            String jwtToken = jwtService.generateToken(userDetails);

            LoggingService.logWithFields(logger, "INFO", "Login successful",
                    Map.of(
                            "userId", user.getId(),
                            "role", user.getRole()
                    ));

            return new AuthResponseDTO(jwtToken, UserDTO.fromEntity(user));

        } catch (Exception e) {
            LoggingService.logError(logger, "user_login", e);
            throw e;
        } finally {
            LoggingService.endOperation(logger, "user_login", startTime);
        }
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        String operationId = LoggingService.startOperation(logger, "user_register");
        long startTime = System.currentTimeMillis();

        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                logger.warn("Email already in use during registration: {}", request.getEmail());
                throw new ExceptionResponse("Email já está em uso");
            }

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role("user")
                    .build();

            User savedUser = userRepository.save(user);

            UserDetails userDetails = createUserDetails(savedUser);
            String jwtToken = jwtService.generateToken(userDetails);

            logger.info("User registered successfully: {}", savedUser.getId());

            return new AuthResponseDTO(jwtToken, UserDTO.fromEntity(savedUser));

        } catch (Exception e) {
            LoggingService.logError(logger, "user_register", e);
            throw e;
        } finally {
            LoggingService.endOperation(logger, "user_register", startTime);
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found in context: {}", email);
                    return new ExceptionResponse("Usuário não encontrado");
                });

        return UserDTO.fromEntity(user);
    }

    private UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
        );
    }
}