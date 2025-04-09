package com.simplesdental.product.service;

import com.simplesdental.product.dto.AuthDTOs.AuthRequestDTO;
import com.simplesdental.product.dto.AuthDTOs.AuthResponseDTO;
import com.simplesdental.product.dto.AuthDTOs.RegisterRequestDTO;
import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ExceptionResponseDTO("Usuário não encontrado"));

        UserDetails userDetails = createUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(jwtToken, UserDTO.fromEntity(user));
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ExceptionResponseDTO("Email já está em uso");
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

        return new AuthResponseDTO(jwtToken, UserDTO.fromEntity(savedUser));
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExceptionResponseDTO("Usuário não encontrado"));

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