package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepositoryImpl userRepository;

    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.addUser(UserMapper.toUser(userDto)));
    }

    public UserDto deleteUser(Long id) {
        return UserMapper.toUserDto(userRepository.deleteUser(id));
    }

    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    public List<UserDto> getUsers() {
        return userRepository
                .getUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public UserDto updateUser(Long id, UserDto updatedUser) {
        return UserMapper.toUserDto(userRepository.updateUser(id, UserMapper.toUser(updatedUser)));
    }
}
