package com.simplesdental.product.service;

import com.simplesdental.product.dto.AuthDTOs.PasswordUpdateDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setPassword("encodedOldPassword");
        user.setName("User Test");
        user.setRole("user");
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {
        PasswordUpdateDTO passwordDTO = new PasswordUpdateDTO();
        passwordDTO.setCurrentPassword("oldPass");
        passwordDTO.setNewPassword("newPass");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updatePassword("user@test.com", passwordDTO);

        assertThat(updatedUser.getPassword()).isEqualTo("encodedNewPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenCurrentPasswordIncorrect() {
        PasswordUpdateDTO passwordDTO = new PasswordUpdateDTO();
        passwordDTO.setCurrentPassword("wrongPass");
        passwordDTO.setNewPassword("newPass");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedOldPassword")).thenReturn(false);

        Throwable thrown = catchThrowable(() -> userService.updatePassword("user@test.com", passwordDTO));
        assertThat(thrown).isInstanceOf(RuntimeException.class).hasMessageContaining("Senha atual incorreta");
    }

    @Test
    void shouldFindAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("user@test.com");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForPasswordUpdate() {
        PasswordUpdateDTO passwordDTO = new PasswordUpdateDTO();
        passwordDTO.setCurrentPassword("oldPass");
        passwordDTO.setNewPassword("newPass");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword("unknown@test.com", passwordDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

}
