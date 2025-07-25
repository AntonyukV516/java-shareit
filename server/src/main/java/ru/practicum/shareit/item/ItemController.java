package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.item.dto.*;

import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemUpdateDto updatedItem,
                              @RequestHeader(Constants.SHARER_USER_ID) Long userId,
                              @PathVariable Long itemId) {
        return itemService.updateItem(updatedItem, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getOwnerItems(@RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
        @PathVariable Long itemId,
        @RequestHeader(Constants.SHARER_USER_ID) Long userId,
        @RequestBody Map<String, String> request) {
            return itemService.addComment(itemId, userId, request.get("text"));
    }
}
