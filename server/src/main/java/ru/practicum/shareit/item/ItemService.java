package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemUpdateDto updatedItem, Long userId, Long itemId);

    ItemResponseDto getItemById(Long itemId);

    List<ItemResponseDto> getOwnerItems(long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, Long userId, String text);
}
