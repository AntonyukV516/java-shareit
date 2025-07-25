package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void addUser_ShouldReturnCreatedUser() {
        UserDto inputDto = new UserDto();
        UserDto expectedDto = new UserDto();
        when(userService.addUser(inputDto)).thenReturn(expectedDto);

        UserDto result = userController.addUser(inputDto);

        assertEquals(expectedDto, result);
        verify(userService).addUser(inputDto);
    }

    @Test
    void deleteUser_ShouldReturnDeletedUser() {
        Long userId = 1L;
        UserDto expectedDto = new UserDto();
        when(userService.deleteUser(userId)).thenReturn(expectedDto);

        UserDto result = userController.deleteUser(userId);

        assertEquals(expectedDto, result);
        verify(userService).deleteUser(userId);
    }

    @Test
    void getUserById_ShouldReturnUser() {
        Long userId = 1L;
        UserDto expectedDto = new UserDto();
        when(userService.getUserById(userId)).thenReturn(expectedDto);

        UserDto result = userController.getUserById(userId);

        assertEquals(expectedDto, result);
        verify(userService).getUserById(userId);
    }

    @Test
    void getUsers_ShouldReturnAllUsers() {
        List<UserDto> expectedList = List.of(
                new UserDto(),
                new UserDto()
        );
        when(userService.getUsers()).thenReturn(expectedList);

        List<UserDto> result = userController.getUsers();

        assertEquals(expectedList, result);
        verify(userService).getUsers();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        Long userId = 1L;
        UserUpdateDto updateDto = new UserUpdateDto();
        UserDto expectedDto = new UserDto();
        when(userService.updateUser(userId, updateDto)).thenReturn(expectedDto);

        UserDto result = userController.updateUser(userId, updateDto);

        assertEquals(expectedDto, result);
        verify(userService).updateUser(userId, updateDto);
    }
}