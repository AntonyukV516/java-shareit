package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.gateway.RestClientUtils;
import ru.practicum.shareit.gateway.user.UserController;
import ru.practicum.shareit.gateway.user.dto.UserDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private RestClientUtils restClient;

    @InjectMocks
    private UserController userController;

    private final Long userId = 1L;
    private final UserDto userDto = new UserDto();
    private final UserUpdateDto userUpdateDto = new UserUpdateDto();

    @Test
    void addUser_ShouldReturnCreatedUser() {
        when(restClient.post(
                eq("/users"),
                any(UserDto.class),
                isNull(),
                eq(UserDto.class))
        ).thenReturn(userDto);

        UserDto response = userController.addUser(userDto);

        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void deleteUser_ShouldReturnDeletedUser() {
        when(restClient.delete(
                eq("/users/{id}"),
                isNull(),
                eq(UserDto.class),
                eq(userId))
        ).thenReturn(userDto);

        UserDto response = userController.deleteUser(userId);

        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(restClient.get(
                eq("/users/{id}"),
                isNull(),
                eq(UserDto.class),
                eq(userId))
        ).thenReturn(userDto);

        UserDto response = userController.getUserById(userId);

        assertNotNull(response);
        assertEquals(userDto, response);
    }

    @Test
    void getUsers_ShouldReturnListOfUsers() {
        UserDto[] usersArray = {userDto, userDto};
        when(restClient.get(
                eq("/users"),
                isNull(),
                eq(UserDto[].class))
        ).thenReturn(usersArray);

        List<UserDto> response = userController.getUsers();

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        when(restClient.patch(
                eq("/users/{id}"),
                any(UserUpdateDto.class),
                isNull(),
                eq(UserDto.class),
                eq(userId))
        ).thenReturn(userDto);

        UserDto response = userController.updateUser(userId, userUpdateDto);

        assertNotNull(response);
        assertEquals(userDto, response);
    }


    @Test
    void getUserById_WhenNotFound_ShouldThrowException() {
        when(restClient.get(
                eq("/users/{id}"),
                isNull(),
                eq(UserDto.class),
                eq(userId))
        ).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(HttpClientErrorException.class,
                () -> userController.getUserById(userId));
    }

    @Test
    void getUsers_WhenNoUsers_ShouldReturnEmptyList() {
        when(restClient.get(
                eq("/users"),
                isNull(),
                eq(UserDto[].class))
        ).thenReturn(new UserDto[0]);

        List<UserDto> response = userController.getUsers();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void updateUser_WithPartialData_ShouldSucceed() {
        UserUpdateDto partialUpdate = new UserUpdateDto();
        partialUpdate.setName("New Name");

        when(restClient.patch(
                anyString(),
                any(UserUpdateDto.class),
                isNull(),
                eq(UserDto.class),
                anyLong()))
                .thenReturn(userDto);

        UserDto response = userController.updateUser(userId, partialUpdate);
        assertNotNull(response);
    }
}
