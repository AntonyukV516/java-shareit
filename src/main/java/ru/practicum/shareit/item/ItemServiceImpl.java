package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        return ItemMapper.toItemDto(itemRepository.addItem(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto updatedItem, Long userId, Long itemId) {
        return ItemMapper.toItemDto(itemRepository.updateItem(ItemMapper.toItem(updatedItem), userId, itemId));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(long userId) {
        return itemRepository
                .getOwnerItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository
                .search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}

