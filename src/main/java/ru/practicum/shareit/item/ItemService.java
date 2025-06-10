package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemUpdateDto updatedItem, Long userId, Long itemId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getOwnerItems(long userId);

    List<ItemDto> search(String text);
}
