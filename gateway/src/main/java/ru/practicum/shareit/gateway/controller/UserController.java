package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.Marker;
import ru.practicum.shareit.gateway.dto.RequestUserDto;
import ru.practicum.shareit.gateway.dto.UserDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<UserDto> add(@Valid @RequestBody RequestUserDto requestUserDto) {
        log.info("Попытка добавить User: {}", requestUserDto);
        return userClient.add(requestUserDto);
    }

    @PatchMapping("/{userId}")
    @Validated(Marker.OnUpdate.class)
    public ResponseEntity<UserDto> updateUser(@PathVariable long userId,
                                              @Valid @RequestBody RequestUserDto requestUserDto) {
        log.info("Попытка обновить User с ID {}: {}", userId, requestUserDto);
        return userClient.updateUser(userId, requestUserDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId) {
        log.info("Попытка удалить User с ID {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable long userId) {
        log.info("Запрос информации о User с ID {}", userId);
        return userClient.getUserById(userId);
    }
}