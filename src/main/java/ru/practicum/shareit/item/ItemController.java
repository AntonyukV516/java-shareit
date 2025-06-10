package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemUpdateDto updatedItem,
                              @RequestHeader ("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId) {
        return itemService.updateItem(updatedItem, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader ("X-Sharer-User-Id") Long userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
