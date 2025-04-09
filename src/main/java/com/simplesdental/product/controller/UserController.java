package com.simplesdental.product.controller;

import com.simplesdental.product.dto.AuthDTOs;
import com.simplesdental.product.dto.ExceptionResponseDTO;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(UserDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) {
        try {
            User savedUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.fromEntity(savedUser));
        } catch (Exception e) {
            throw new ExceptionResponseDTO("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            return userService.findById(id)
                    .map(existingUser -> {
                        user.setId(id);
                        User updatedUser = userService.save(user);
                        return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
                    })
                    .orElseThrow(() -> new ExceptionResponseDTO("Usuário não encontrado com o ID: " + id));
        } catch (Exception e) {
            throw new ExceptionResponseDTO("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    userService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
