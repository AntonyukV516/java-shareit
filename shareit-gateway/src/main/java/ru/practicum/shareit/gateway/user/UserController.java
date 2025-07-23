package ru.practicum.shareit.gateway.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.RestClientUtils;
import ru.practicum.shareit.gateway.user.dto.UserDto;
import ru.practicum.shareit.gateway.user.dto.UserUpdateDto;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final RestClientUtils restClient;

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        return restClient.post(
                "/users",
                userDto,
                null,
                UserDto.class
        );
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable Long id) {
        return restClient.delete(
                "/users/{id}",
                null,
                UserDto.class,
                id
        );
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return restClient.get(
                "/users/{id}",
                null,
                UserDto.class,
                id
        );
    }

    @GetMapping
    public List<UserDto> getUsers() {
        UserDto[] response = restClient.get(
                "/users",
                null,
                UserDto[].class
        );
        return Arrays.asList(response);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDto updatedUser) {
        return restClient.patch(
                "/users/{id}",
                updatedUser,
                null,
                UserDto.class,
                id
        );
    }
}