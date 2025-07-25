package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final ItemDto itemDto = new ItemDto();
    private final ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
    private final ItemResponseDto itemResponseDto = new ItemResponseDto();
    private final CommentDto commentDto = new CommentDto();

    @Test
    void addItem_ShouldReturnCreatedItem() {
        when(itemService.addItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        ItemDto result = itemController.addItem(itemDto, userId);

        assertNotNull(result);
        assertEquals(itemDto, result);
        verify(itemService).addItem(itemDto, userId);
    }

    @Test
    void addItem_ShouldReturnCreatedStatus() {
        when(itemService.addItem(any(), anyLong())).thenReturn(itemDto);

        ItemDto result = itemController.addItem(itemDto, userId);

        assertEquals(HttpStatus.CREATED.value(), 201);
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() {
        when(itemService.updateItem(any(ItemUpdateDto.class), anyLong(), anyLong()))
                .thenReturn(itemDto);

        ItemDto result = itemController.updateItem(itemUpdateDto, userId, itemId);

        assertNotNull(result);
        assertEquals(itemDto, result);
        verify(itemService).updateItem(itemUpdateDto, userId, itemId);
    }

    @Test
    void getItemById_ShouldReturnItem() {
        when(itemService.getItemById(anyLong()))
                .thenReturn(itemResponseDto);

        ItemResponseDto result = itemController.getItemById(itemId);

        assertNotNull(result);
        assertEquals(itemResponseDto, result);
        verify(itemService).getItemById(itemId);
    }

    @Test
    void getOwnerItems_ShouldReturnList() {
        when(itemService.getOwnerItems(anyLong()))
                .thenReturn(List.of(itemResponseDto));

        List<ItemResponseDto> result = itemController.getOwnerItems(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemResponseDto, result.getFirst());
        verify(itemService).getOwnerItems(userId);
    }

    @Test
    void search_ShouldReturnList() {
        String searchText = "test";
        when(itemService.search(anyString()))
                .thenReturn(List.of(itemDto));

        List<ItemDto> result = itemController.search(searchText);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto, result.getFirst());
        verify(itemService).search(searchText);
    }

    @Test
    void addComment_ShouldReturnComment() {
        String commentText = "Great item!";
        Map<String, String> request = Map.of("text", commentText);

        when(itemService.addComment(anyLong(), anyLong(), anyString()))
                .thenReturn(commentDto);

        CommentDto result = itemController.addComment(itemId, userId, request);

        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(itemService).addComment(itemId, userId, commentText);
    }
}
