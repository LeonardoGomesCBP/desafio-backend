package com.simplesdental.product.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthRequestDTO {
        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Email deve ser válido")
        private String email;

        @NotBlank(message = "Senha não pode ser vazia")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponseDTO {
        private String token;
        private UserDTO user;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequestDTO {
        @NotBlank(message = "Nome não pode ser vazio")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        private String name;

        @NotBlank(message = "Email não pode ser vazio")
        @Email(message = "Email deve ser válido")
        private String email;

        @NotBlank(message = "Senha não pode ser vazia")
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordUpdateDTO {
        @NotBlank(message = "Senha atual não pode ser vazia")
        private String currentPassword;

        @NotBlank(message = "Nova senha não pode ser vazia")
        @Size(min = 6, message = "Nova senha deve ter pelo menos 6 caracteres")
        private String newPassword;
    }
}
