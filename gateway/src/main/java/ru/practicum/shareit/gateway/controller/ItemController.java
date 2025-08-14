package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.gateway.client.ItemClient;
import ru.practicum.shareit.gateway.dto.*;

import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ResponseEntity<ItemDto> add(@Valid @RequestBody ItemBodyDto itemBodyDto,
                                       @RequestHeader(Constants.SHARER_USER_ID) long userId) {
        log.info("Запрос на добавление элемента: {}, для пользователя с ID {}", itemBodyDto, userId);
        return itemClient.add(itemBodyDto, userId);
    }

    @PatchMapping("/{itemsId}")
    public ResponseEntity<ItemDto> update(@PathVariable long itemsId,
                                          @Valid @RequestBody ItemBodyDto itemBodyDto,
                                          @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        log.info("Запрос на обновление элемента с ID {}: {}, для пользователя с ID {}",
                itemsId, itemBodyDto, userId);
        return itemClient.update(itemsId, itemBodyDto, userId);
    }

    @GetMapping("/{itemsId}")
    public ResponseEntity<ItemDto> getById(@PathVariable long itemsId) {
        log.info("Запрос на получение элемента с ID {}", itemsId);
        return itemClient.getById(itemsId);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getItemsByOwner(
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение элементов для владельца с ID {}", userId);
        return itemClient.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        log.info("Запрос на поиск элементов по тексту '{}' для пользователя с ID {}", text, userId);
        return itemClient.search(text, userId);
    }

    @PostMapping("{itemId}/comment")
    @Validated
    public ResponseEntity<CommentDto> addComment(@PathVariable Long itemId,
                                                 @Valid @RequestBody RequestCommentDto requestCommentDto,
                                                 @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        log.info("Запрос на добавление комментария к элементу с ID {}: {}, для пользователя с ID {}",
                itemId, requestCommentDto, userId);
        return itemClient.addComment(itemId, requestCommentDto, userId);
    }
}
