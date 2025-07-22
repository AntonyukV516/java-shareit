package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static ItemResponseDto toItemResponseDto(Item item,
                                                    Booking lastBooking,
                                                    Booking nextBooking,
                                                    List<CommentDto> comments) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setComments(comments);

        if (lastBooking != null) {
            dto.setLastBooking(lastBooking.getStart());
        }

        if (nextBooking != null) {
            dto.setNextBooking(nextBooking.getStart());
        }

        return dto;
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null);
    }

    public static Item toItem(ItemDto itemDto, ItemRequest request) {
        return new Item(null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                request);
    }
}
