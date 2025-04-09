package com.simplesdental.product.service;

import com.simplesdental.product.dto.AuthDTOs.PasswordUpdateDTO;
import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User save(User user) {
        if (user.getId() == null && userRepository.existsByEmail(user.getEmail())) {
            throw new ExceptionResponseDTO("Email já está em uso: " + user.getEmail());
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty() &&
                (user.getId() == null || !user.getPassword().startsWith("$2a$"))) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User updatePassword(String email, PasswordUpdateDTO passwordDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ExceptionResponseDTO("Usuário não encontrado"));

        if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
            throw new ExceptionResponseDTO("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        return userRepository.save(user);
    }
}