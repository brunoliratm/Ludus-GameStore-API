package com.ludus.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import com.ludus.dtos.requests.AuthDto;
import com.ludus.enums.UserRole;
import com.ludus.exceptions.InvalidCredentialsException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.exceptions.UserInactiveException;
import com.ludus.models.UserModel;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private UserModel activeUser;
    private UserModel inactiveUser;

    @BeforeEach
    void setUp() {
        activeUser = new UserModel();
        activeUser.setId(1L);
        activeUser.setEmail("user@example.com");
        activeUser.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyz123456789");
        activeUser.setName("Test User");
        activeUser.setRole(UserRole.USER);
        activeUser.setActive(true);

        inactiveUser = new UserModel();
        inactiveUser.setId(2L);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyz123456789");
        inactiveUser.setName("Inactive User");
        inactiveUser.setRole(UserRole.USER);
        inactiveUser.setActive(false);
    }

    @Test
    void loadUserByUsername_ExistingEmail_ReturnsUserDetails() {
        when(userService.loadUserByEmail("user@example.com")).thenReturn(activeUser);
        var result = authService.loadUserByUsername("user@example.com");

        assertNotNull(result);
        assertEquals("user@example.com", result.getUsername());
        verify(userService).loadUserByEmail("user@example.com");
    }

    @Test
    void login_NullEmail_ThrowsInvalidCredentialsException() {
        AuthDto nullEmailDto = new AuthDto(null, "password123");

        assertThrows(InvalidCredentialsException.class, () -> authService.login(nullEmailDto, authenticationManager));
    }

    @Test
    void login_NullPassword_ThrowsInvalidCredentialsException() {
        AuthDto nullPasswordDto = new AuthDto("user@example.com", null);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(nullPasswordDto, authenticationManager));
    }

    @Test
    void login_InvalidEmailFormat_ThrowsInvalidCredentialsException() {
        AuthDto invalidEmailDto = new AuthDto("invalid-email", "password123");

        assertThrows(InvalidCredentialsException.class, () -> authService.login(invalidEmailDto, authenticationManager));
    }

    @Test
    void login_NonExistingEmail_ThrowsNotFoundException() {
        AuthDto nonExistingEmailDto = new AuthDto("nonexisting@example.com", "password123");
        when(userService.findByEmail("nonexisting@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(nonExistingEmailDto, authenticationManager));
    }

    @Test
    void login_InactiveUser_ThrowsUserInactiveException() {
        AuthDto inactiveUserDto = new AuthDto("inactive@example.com", "password123");
        when(userService.findByEmail("inactive@example.com")).thenReturn(Optional.of(inactiveUser));

        assertThrows(UserInactiveException.class, () -> authService.login(inactiveUserDto, authenticationManager));
    }

}