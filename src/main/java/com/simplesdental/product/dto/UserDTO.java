package com.simplesdental.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.simplesdental.product.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String email;
    private String role;

    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}