package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null);
    }

    public static Item toItem(ItemUpdateDto itemUpdateDto) {
        return new Item(null,
                itemUpdateDto.getName(),
                itemUpdateDto.getDescription(),
                itemUpdateDto.getAvailable(),
                null);
    }
}
