package com.simplesdental.product.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractEntity<Long> {

    @NotBlank(message = "Nome não pode ser vazio.")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres.")
    private String name;

    @NotBlank(message = "Email não pode ser vazio.")
    @Email(message = "Email deve ser válido.")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Senha não pode ser vazia.")
    private String password;

    @NotBlank(message = "Role não pode ser vazia.")
    @Pattern(regexp = "^(admin|user)$", message = "Role deve ser 'admin' ou 'user'")
    @Column(name = "role")
    private String role;
}