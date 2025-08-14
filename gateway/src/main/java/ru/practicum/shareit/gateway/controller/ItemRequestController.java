package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.gateway.client.ItemRequestClient;
import ru.practicum.shareit.gateway.dto.Marker;
import ru.practicum.shareit.gateway.dto.RequestDto;
import ru.practicum.shareit.gateway.dto.RequestItemDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;


    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<RequestDto> addItemRequest(@Valid @RequestBody RequestItemDto requestItemDto,
                                                     @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        log.info("GATEWAY Попытка добавить Request");
        return itemRequestClient.addItemRequest(requestItemDto, userId);
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getUserItemRequests(
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getOtherUsersItemRequests(
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return itemRequestClient.getOtherUsersItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getItemRequestById(
            @RequestHeader(Constants.SHARER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        log.info("GATEWAY Попытка получить Request по id");
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
