package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item, Long userId);

    Item updateItem(ItemUpdateDto updatedItem, Long userId, Long itemId);

    Item getItemById(Long itemId);

    List<Item> getOwnerItems(long userId);

    List<Item> search(String text);
}
