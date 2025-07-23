package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(CreateItemRequestDto dto, User requester) {
        return new ItemRequest(null,
                dto.getDescription(),
                requester,
                LocalDateTime.now());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                Collections.emptyList()
        );
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        return new ItemForRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getAvailable());
    }
}
