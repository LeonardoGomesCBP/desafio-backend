package com.simplesdental.product.controller;

import com.simplesdental.product.dto.AuthDTOs;
import com.simplesdental.product.dto.AuthDTOs.AuthRequestDTO;
import com.simplesdental.product.dto.AuthDTOs.AuthResponseDTO;
import com.simplesdental.product.dto.AuthDTOs.RegisterRequestDTO;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.service.AuthService;
import com.simplesdental.product.service.LoggingService;
import com.simplesdental.product.service.UserService;
import com.simplesdental.product.utils.doc.AuthApiResponses.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Operações relacionadas a autenticação e gerenciamento de usuários")
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
    @Operation(summary = "Realizar login do usuário", description = "Autentica o usuário e retorna um token JWT")
    @SwaggerResponseLoginSuccess
    @SwaggerResponseValidationError
    public ResponseEntity<AuthResponseDTO> login(
            @Valid
            @RequestBody
            AuthRequestDTO request
    ) {
        logger.info("Login request received: {}", request.getEmail());
        AuthResponseDTO response = authService.login(request);
        logger.info("Login successful: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário e retorna um token JWT")
    @SwaggerResponseRegisterSuccess
    @SwaggerResponseValidationError
    @SwaggerResponseBusinessError
    public ResponseEntity<AuthResponseDTO> register(
            @Valid
            @RequestBody
            RegisterRequestDTO request
    ) {
        logger.info("Registration request received: {}", request.getEmail());
        AuthResponseDTO response = authService.register(request);
        logger.info("Registration successful: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/context")
    @Operation(summary = "Obter contexto do usuário autenticado", description = "Retorna as informações do usuário autenticado")
    @SwaggerResponseContextSuccess
    @SwaggerResponseUnauthorized
    @Cacheable(value = "userContext", key = "#authentication.name", cacheManager = "redisCacheManager")
    public UserDTO getContext(Authentication authentication) {
        return authService.getCurrentUser();
    }

    @PutMapping("/password")
    @Operation(summary = "Atualizar senha do usuário", description = "Permite ao usuário autenticado atualizar sua senha")
    @SwaggerResponsePasswordUpdated
    @SwaggerResponseValidationError
    @SwaggerResponseUnauthorized
    @CacheEvict(value = "userContext", key = "#authentication.name", cacheManager = "redisCacheManager")
    public ResponseEntity<String> updatePassword(
            Authentication authentication,
            @Valid
            @RequestBody
            AuthDTOs.PasswordUpdateDTO passwordDTO
    ) {
        String email = authentication.getName();
        userService.updatePassword(email, passwordDTO);
        return ResponseEntity.ok().body("Senha atualizada com sucesso");
    }
}
