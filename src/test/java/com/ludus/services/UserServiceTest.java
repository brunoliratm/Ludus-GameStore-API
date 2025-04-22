package com.ludus.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import com.ludus.dtos.responses.ApiDtoResponse;
import com.ludus.dtos.responses.InfoDtoResponse;
import com.ludus.dtos.responses.UserDtoResponse;
import com.ludus.enums.UserRole;
import com.ludus.exceptions.InvalidIdException;
import com.ludus.exceptions.InvalidPageException;
import com.ludus.exceptions.NotFoundException;
import com.ludus.models.UserModel;
import com.ludus.repositories.UserRepository;
import com.ludus.utils.UtilHelper;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private UtilHelper utilHelper;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserService userService;

    private UserModel testUser;
    private List<UserModel> userList;

    @BeforeEach
    void setUp() {
        testUser = new UserModel();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(UserRole.USER);
        testUser.setActive(true);

        userList = new ArrayList<>();
        userList.add(testUser);

        UserModel user2 = new UserModel();
        user2.setId(2L);
        user2.setName("Second User");
        user2.setEmail("second@example.com");
        user2.setPassword("password456");
        user2.setRole(UserRole.USER);
        user2.setActive(true);
        userList.add(user2);
    }

    @Test
    void getAllUsers_ValidPageAndName_ReturnsApiDtoResponse() {
        Page<UserModel> userPage = new PageImpl<>(userList);
        InfoDtoResponse mockInfo = new InfoDtoResponse(2L, 1L, null, null);
        
        when(userRepository.findAll(any(), any(Pageable.class))).thenReturn(userPage);
        when(utilHelper.buildPageableInfoDto(any(), anyString())).thenReturn(mockInfo);
        ApiDtoResponse<UserDtoResponse> result = userService.getAllUsers(1, null);

        assertNotNull(result);
        assertEquals(2, result.results().size());
        assertEquals(1, result.results().get(0).id());
        assertEquals("Test User", result.results().get(0).name());
        assertEquals(mockInfo, result.info());
        
        verify(userRepository).findAll(eq(null), any(Pageable.class));
        verify(utilHelper).buildPageableInfoDto(eq(userPage), eq("/users"));
    }

    @Test
    void getAllUsers_InvalidPage_ThrowsInvalidPageException() {
        assertThrows(InvalidPageException.class, () -> userService.getAllUsers(0, null));
        assertThrows(InvalidPageException.class, () -> userService.getAllUsers(-1, null));
    }

    @Test
    void getUserById_ExistingId_ReturnsUserDtoResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        UserDtoResponse result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test User", result.name());
        assertEquals("test@example.com", result.email());
        
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_InvalidId_ThrowsInvalidIdException() {
        assertThrows(InvalidIdException.class, () -> userService.getUserById(null));
        assertThrows(InvalidIdException.class, () -> userService.getUserById(0L));
        assertThrows(InvalidIdException.class, () -> userService.getUserById(-1L));
    }

    @Test
    void getUserById_NonExistingId_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("user.not.found"), any(), any(Locale.class))).thenReturn("User not found");

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void getUserById_InactiveUser_ThrowsNotFoundException() {
        UserModel inactiveUser = new UserModel();
        inactiveUser.setId(3L);
        inactiveUser.setActive(false);
        when(userRepository.findById(3L)).thenReturn(Optional.of(inactiveUser));
        when(messageSource.getMessage(eq("user.not.found"), any(), any(Locale.class))).thenReturn("User not found");

        assertThrows(NotFoundException.class, () -> userService.getUserById(3L));
        verify(userRepository).findById(3L);
    }

    @Test
    void deleteUser_ExistingId_SetUserInactive() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        userService.deleteUser(1L);
        assertFalse(testUser.isActive());
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_InvalidId_ThrowsInvalidIdException() {
        assertThrows(InvalidIdException.class, () -> userService.deleteUser(null));
        assertThrows(InvalidIdException.class, () -> userService.deleteUser(0L));
        assertThrows(InvalidIdException.class, () -> userService.deleteUser(-1L));
    }

    @Test
    void deleteUser_NonExistingId_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("user.not.found"), any(), any(Locale.class))).thenReturn("User not found");

        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
        verify(userRepository).findById(999L);
    }
}