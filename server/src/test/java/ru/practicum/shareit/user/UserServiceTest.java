package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @Transactional
    void addUser_ShouldSaveAndReturnUserDto() {
        UserDto inputDto = new UserDto(null, "Test User", "test@example.com");
        User user = User.builder().email("test@example.com").name("Test User").build();
        User savedUser = User.builder().id(1L).email("test@example.com").name("Test User").build();

        when(userRepository.existsByEmail(inputDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.addUser(inputDto);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(inputDto.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(inputDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Transactional
    void addUser_ShouldThrowWhenEmailExists() {
        UserDto inputDto = new UserDto(null, "existing@example.com", "Test User");
        when(userRepository.existsByEmail(inputDto.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.addUser(inputDto));
        verify(userRepository).existsByEmail(inputDto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    @Transactional
    void deleteUser_ShouldDeleteAndReturnDeletedUser() {
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@example.com").name("Test User").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.deleteUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    @Transactional
    void deleteUser_ShouldThrowWhenUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void getUserById_ShouldReturnUserDto() {
        Long userId = 1L;
        User user = User.builder().id(userId).email("test@example.com").name("Test User").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowWhenUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void getUsers_ShouldReturnListOfUserDtos() {
        User user1 = User.builder().id(1L).email("test1@example.com").name("User 1").build();
        User user2 = User.builder().id(2L).email("test2@example.com").name("User 2").build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @Transactional
    void updateUser_ShouldUpdateAndReturnUpdatedUser() {
        Long userId = 1L;
        UserUpdateDto updateDto = new UserUpdateDto(null, "new@example.com", "New Name");
        User existingUser = User.builder().id(userId).email("old@example.com").name("Old Name").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(updateDto.getEmail(), userId)).thenReturn(false);
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getEmail(), result.getEmail());
        assertEquals(updateDto.getName(), result.getName());
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNot(updateDto.getEmail(), userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    @Transactional
    void updateUser_ShouldThrowWhenEmailExists() {
        Long userId = 1L;
        UserUpdateDto updateDto = new UserUpdateDto(null, "existing@example.com", "New Name");
        User existingUser = User.builder().id(userId).email("old@example.com").name("Old Name").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(updateDto.getEmail(), userId)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.updateUser(userId, updateDto));
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNot(updateDto.getEmail(), userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @Transactional
    void updateUser_ShouldUpdateOnlyNameWhenEmailIsNull() {
        Long userId = 1L;
        UserUpdateDto updateDto = new UserUpdateDto(null, "New Name", null);
        User existingUser = User.builder().id(userId).email("old@example.com").name("Old Name").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto result = userService.updateUser(userId, updateDto);

        assertNotNull(result);
        assertEquals(existingUser.getEmail(), result.getEmail());
        assertEquals(updateDto.getName(), result.getName());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmailAndIdNot(any(), any());
        verify(userRepository).save(existingUser);
    }
}
