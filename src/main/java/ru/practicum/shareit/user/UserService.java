package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto addUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email уже используется другим пользователем");
        }
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public UserDto deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден с id " + id));
        userRepository.delete(user);
        return UserMapper.toUserDto(user);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден с id " + id));
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public UserDto updateUser(Long id, UserUpdateDto updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    if (updatedUser.getEmail() != null) {
                        if (userRepository.existsByEmailAndIdNot(updatedUser.getEmail(), id)) {
                            throw new RuntimeException("Email уже используется другим пользователем");
                        }
                        existingUser.setEmail(updatedUser.getEmail());
                    }

                    if (updatedUser.getName() != null) {
                        existingUser.setName(updatedUser.getName());
                    }

                    return UserMapper.toUserDto(userRepository.save(existingUser));
                })
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id " + id));
    }
}
