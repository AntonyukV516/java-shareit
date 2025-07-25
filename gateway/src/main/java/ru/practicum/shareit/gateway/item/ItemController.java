package ru.practicum.shareit.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.gateway.RestClientUtils;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.ItemResponseDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final RestClientUtils restClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return restClient.post(
                "/items",
                itemDto,
                userId,
                ItemDto.class
        );
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestBody @Valid ItemUpdateDto updatedItem,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        return restClient.patch(
                "/items/{itemId}",
                updatedItem,
                userId,
                ItemDto.class,
                itemId
        );
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(
            @PathVariable Long itemId,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return restClient.get(
                "/items/{itemId}",
                userId,
                ItemResponseDto.class,
                itemId
        );
    }

    @GetMapping
    public List<ItemResponseDto> getOwnerItems(
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        ItemResponseDto[] response = restClient.get(
                "/items",
                userId,
                ItemResponseDto[].class
        );
        return Arrays.asList(response);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        ItemDto[] response = restClient.get(
                "/items/search?text={text}",
                userId,
                ItemDto[].class,
                text
        );
        return Arrays.asList(response);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable Long itemId,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId,
            @RequestBody Map<String, String> request) {
        return restClient.post(
                "/items/{itemId}/comment",
                request,
                userId,
                CommentDto.class,
                itemId
        );
    }
}