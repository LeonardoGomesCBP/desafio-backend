package com.simplesdental.product.controller;

import com.simplesdental.product.dto.AuthDTOs;
import com.simplesdental.product.dto.AuthDTOs.AuthRequestDTO;
import com.simplesdental.product.dto.AuthDTOs.AuthResponseDTO;
import com.simplesdental.product.dto.AuthDTOs.RegisterRequestDTO;
import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.service.AuthService;
import com.simplesdental.product.service.LoggingService;
import com.simplesdental.product.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.annotation.Cacheable;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final CacheManager redisCacheManager;
    private final Logger logger = LoggingService.getLogger(AuthController.class);

    @Autowired
    public AuthController(
            AuthService authService,
            UserService userService,
            @Qualifier("redisCacheManager") CacheManager redisCacheManager
    ) {
        this.authService = authService;
        this.userService = userService;
        this.redisCacheManager = redisCacheManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        logger.info("Login request received: {}", request.getEmail());

        try {
            AuthResponseDTO response = authService.login(request);
            logger.info("Login successful: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Login failed: {} - {}", request.getEmail(), e.getMessage());
            throw new ExceptionResponseDTO("Falha na autenticação: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        logger.info("Registration request received: {}", request.getEmail());

        try {
            AuthResponseDTO response = authService.register(request);
            logger.info("Registration successful: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Registration failed: {} - {}", request.getEmail(), e.getMessage());
            throw new ExceptionResponseDTO("Falha no registro: " + e.getMessage());
        }
    }

    @GetMapping("/context")
    @Cacheable(value = "userContext", key = "#authentication.name", cacheManager = "redisCacheManager")
    public UserDTO getContext(Authentication authentication) {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting user context for: {}", authentication.getName());
        }
        return authService.getCurrentUser();
    }

    @PutMapping("/password")
    @CacheEvict(value = "userContext", key = "#authentication.name", cacheManager = "redisCacheManager")
    public ResponseEntity<String> updatePassword(
            Authentication authentication,
            @Valid @RequestBody AuthDTOs.PasswordUpdateDTO passwordDTO
    ) {
        String email = authentication.getName();
        logger.info("Password change request for: {}", email);

        try {
            userService.updatePassword(email, passwordDTO);
            logger.info("Password change successful for: {}", email);
            return ResponseEntity.ok().body("Senha atualizada com sucesso");
        } catch (Exception e) {
            logger.warn("Password change failed for: {} - {}", email, e.getMessage());
            throw new ExceptionResponseDTO("Erro ao atualizar senha: " + e.getMessage());
        }
    }
}