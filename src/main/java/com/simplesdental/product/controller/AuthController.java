package com.simplesdental.product.controller;

import com.simplesdental.product.dto.AuthDTOs;
import com.simplesdental.product.dto.AuthDTOs.AuthRequestDTO;
import com.simplesdental.product.dto.AuthDTOs.AuthResponseDTO;
import com.simplesdental.product.dto.AuthDTOs.RegisterRequestDTO;
import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.service.AuthService;
import com.simplesdental.product.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.cache.annotation.Cacheable;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final CacheManager redisCacheManager;

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
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ExceptionResponseDTO("Falha na autenticação: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            AuthResponseDTO response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ExceptionResponseDTO("Falha no registro: " + e.getMessage());
        }
    }

    @GetMapping("/context")
    @Cacheable(value = "userContext", key = "#authentication.name", cacheManager = "redisCacheManager")
    public UserDTO getContext(Authentication authentication) {
        return authService.getCurrentUser();
    }


    @PutMapping("/password")
    @CacheEvict(value = "userContext", key = "#authentication.name", cacheManager = "redisCacheManager")
    public ResponseEntity<String> updatePassword(
            Authentication authentication,
            @Valid @RequestBody AuthDTOs.PasswordUpdateDTO passwordDTO
    ) {
        try {
            String email = authentication.getName();
            userService.updatePassword(email, passwordDTO);
            return ResponseEntity.ok().body("Senha atualizada com sucesso");
        } catch (Exception e) {
            throw new ExceptionResponseDTO("Erro ao atualizar senha: " + e.getMessage());
        }
    }
}