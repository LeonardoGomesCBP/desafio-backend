package com.simplesdental.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplesdental.product.config.TestConfig;
import com.simplesdental.product.dto.AuthDTOs;
import com.simplesdental.product.dto.AuthDTOs.AuthRequestDTO;
import com.simplesdental.product.dto.AuthDTOs.AuthResponseDTO;
import com.simplesdental.product.dto.AuthDTOs.RegisterRequestDTO;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.service.AuthService;
import com.simplesdental.product.service.JwtService;
import com.simplesdental.product.service.LoggingService;
import com.simplesdental.product.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.eq;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private LoggingService loggingService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    @org.springframework.beans.factory.annotation.Qualifier("redisCacheManager")
    private CacheManager redisCacheManager;

    private AuthRequestDTO authRequest;
    private RegisterRequestDTO registerRequest;
    private AuthResponseDTO authResponse;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequestDTO();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        registerRequest = new RegisterRequestDTO();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setRole("user");

        authResponse = new AuthResponseDTO("dummyToken", userDTO);
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        when(authService.login(any(AuthRequestDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummyToken"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.name").value("Test User"));
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        when(authService.login(any(AuthRequestDTO.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummyToken"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.name").value("Test User"));
    }

    @Test
    @WithMockUser
    void shouldGetUserContext() throws Exception {
        Cache mockCache = mock(Cache.class);
        when(redisCacheManager.getCache("userContext")).thenReturn(mockCache);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");

        when(authService.getCurrentUser()).thenReturn(userDTO);

        mockMvc.perform(get("/auth/context"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdatePasswordSuccessfully() throws Exception {
        Cache mockCache = mock(Cache.class);
        when(redisCacheManager.getCache("userContext")).thenReturn(mockCache);

        AuthDTOs.PasswordUpdateDTO passwordDTO = new AuthDTOs.PasswordUpdateDTO();
        passwordDTO.setCurrentPassword("oldPass");
        passwordDTO.setNewPassword("newPass");

        User user = new User();
        user.setEmail("test@example.com");

        when(userService.updatePassword(eq("test@example.com"), any(AuthDTOs.PasswordUpdateDTO.class))).thenReturn(user);

        mockMvc.perform(put("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha atualizada com sucesso"));
    }

}
