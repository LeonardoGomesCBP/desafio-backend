package com.simplesdental.product.service;

import com.simplesdental.product.dto.AuthDTOs.AuthRequestDTO;
import com.simplesdental.product.dto.AuthDTOs.RegisterRequestDTO;
import com.simplesdental.product.dto.AuthDTOs.AuthResponseDTO;
import com.simplesdental.product.dto.ExceptionResponse;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private AuthRequestDTO authRequest;
    private RegisterRequestDTO registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequestDTO();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        registerRequest = new RegisterRequestDTO();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole("user");
    }

    @Test
    void shouldLoginSuccessfully() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("dummyToken");

        AuthResponseDTO response = authService.login(authRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("dummyToken");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("dummyToken");

        AuthResponseDTO response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("dummyToken");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("test@example.com", null));

        assertThatThrownBy(() -> authService.login(authRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao tentar realizar login")
                .hasCauseInstanceOf(ExceptionResponse.class);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ExceptionResponse.class)
                .hasMessageContaining("Email já está em uso");
    }

    @Test
    void shouldGetCurrentUser() {
        Authentication auth = new UsernamePasswordAuthenticationToken("test@example.com", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO result = authService.getCurrentUser();

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }
}