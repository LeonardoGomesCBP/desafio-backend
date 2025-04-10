package com.simplesdental.product.service;

import com.simplesdental.product.dto.AuthDTOs.PasswordUpdateDTO;
import com.simplesdental.product.dto.ExceptionResponse;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggingService.getLogger(UserService.class);

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
        try {
            if (user.getId() == null) {
                LoggingService.logWithFields(logger, "INFO", "Creating new user",
                        Map.of(
                                "email", user.getEmail(),
                                "role", user.getRole()
                        ));

                if (userRepository.existsByEmail(user.getEmail())) {
                    LoggingService.logWithField(logger, "WARN", "User registration failed - email already exists", "email", user.getEmail());
                    throw new ExceptionResponse("Email já está em uso: " + user.getEmail());
                }
            } else {
                logger.info("Updating existing user: {}", user.getId());
            }

            if (user.getPassword() != null && !user.getPassword().isEmpty() &&
                    (user.getId() == null || !user.getPassword().startsWith("$2a$"))) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            User savedUser = userRepository.save(user);
            logger.info("User saved successfully: {}", savedUser.getId());
            return savedUser;

        } catch (Exception e) {
            LoggingService.logError(logger, "user_save", e);
            throw e;
        }
    }

    @Transactional
    public void deleteById(Long id) {
        logger.info("Deleting user: {}", id);
        try {
            userRepository.deleteById(id);
            logger.info("User deleted successfully: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", id, e);
            throw e;
        }
    }

    @Transactional
    public User updatePassword(String email, PasswordUpdateDTO passwordDTO) {
        logger.info("Password change requested for user: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Password change failed - user not found: {}", email);
                        return new ExceptionResponse("Usuário não encontrado");
                    });

            if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())) {
                logger.warn("Password change failed - incorrect current password: {}", email);
                throw new ExceptionResponse("Senha atual incorreta");
            }

            user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            User updatedUser = userRepository.save(user);
            logger.info("Password changed successfully for user: {}", email);
            return updatedUser;

        } catch (Exception e) {
            LoggingService.logError(logger, "password_update", e);
            throw e;
        }
    }
}