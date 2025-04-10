package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ExceptionResponse;
import com.simplesdental.product.dto.UserDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.service.UserService;
import com.simplesdental.product.utils.doc.UserApiResponses.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Usuários", description = "Gerenciamento de usuários (apenas administradores)")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna a lista de todos os usuários cadastrados")
    @SwaggerResponseGetAll
    public List<UserDTO> getAllUsers() {
        return userService.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário pelo seu identificador")
    @SwaggerResponseGetById
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(UserDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @SwaggerResponseCreate
    public ResponseEntity<UserDTO> createUser(
            @Valid
            @RequestBody
            User user
    ) {
        User savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDTO.fromEntity(savedUser));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @SwaggerResponseUpdate
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid
            @RequestBody
            User user
    ) {
        return userService.findById(id)
                .map(existingUser -> {
                    user.setId(id);
                    User updatedUser = userService.save(user);
                    return ResponseEntity.ok(UserDTO.fromEntity(updatedUser));
                })
                .orElseThrow(() -> new ExceptionResponse("Usuário não encontrado com o ID: " + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove um usuário do sistema pelo ID")
    @SwaggerResponseDelete
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    userService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
