package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.gateway.RestClientUtils;
import ru.practicum.shareit.gateway.item.ItemController;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.item.dto.ItemResponseDto;
import ru.practicum.shareit.gateway.item.dto.ItemUpdateDto;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private RestClientUtils restClient;

    @InjectMocks
    private ItemController itemController;

    private final Long userId = 1L;
    private final Long itemId = 10L;
    private final ItemDto itemDto = new ItemDto();
    private final ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
    private final ItemResponseDto itemResponseDto = new ItemResponseDto();
    private final CommentDto commentDto = new CommentDto();

    @Test
    void addItem_ShouldReturnCreatedItem() {
        when(restClient.post(eq("/items"), any(ItemDto.class), eq(userId), eq(ItemDto.class)))
                .thenReturn(itemDto);

        ItemDto response = itemController.addItem(itemDto, userId);

        assertNotNull(response);
        assertEquals(itemDto, response);
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() {
        when(restClient.patch(eq("/items/{itemId}"), any(ItemUpdateDto.class), eq(userId), eq(ItemDto.class), eq(itemId)))
                .thenReturn(itemDto);

        ItemDto response = itemController.updateItem(itemUpdateDto, userId, itemId);

        assertNotNull(response);
        assertEquals(itemDto, response);
    }

    @Test
    void getItemById_ShouldReturnItem() {
        when(restClient.get(eq("/items/{itemId}"), eq(userId), eq(ItemResponseDto.class), eq(itemId)))
                .thenReturn(itemResponseDto);

        ItemResponseDto response = itemController.getItemById(itemId, userId);

        assertNotNull(response);
        assertEquals(itemResponseDto, response);
    }

    @Test
    void getOwnerItems_ShouldReturnListOfItems() {
        ItemResponseDto[] itemsArray = {itemResponseDto};
        when(restClient.get(eq("/items"), eq(userId), eq(ItemResponseDto[].class)))
                .thenReturn(itemsArray);

        List<ItemResponseDto> response = itemController.getOwnerItems(userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(itemResponseDto, response.getFirst());
    }

    @Test
    void search_ShouldReturnListOfItems() {
        String searchText = "test";
        ItemDto[] itemsArray = {itemDto};
        when(restClient.get(eq("/items/search?text={text}"), eq(userId), eq(ItemDto[].class), eq(searchText)))
                .thenReturn(itemsArray);

        List<ItemDto> response = itemController.search(searchText, userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(itemDto, response.getFirst());
    }

    @Test
    void addComment_ShouldReturnCreatedComment() {
        Map<String, String> request = Map.of("text", "Test comment");
        when(restClient.post(eq("/items/{itemId}/comment"), eq(request), eq(userId), eq(CommentDto.class), eq(itemId)))
                .thenReturn(commentDto);

        CommentDto response = itemController.addComment(itemId, userId, request);

        assertNotNull(response);
        assertEquals(commentDto, response);
    }

    @Test
    void getItemById_WhenNotFound_ShouldThrowException() {
        when(restClient.get(eq("/items/{itemId}"), eq(userId), eq(ItemResponseDto.class), eq(itemId)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(HttpClientErrorException.class,
                () -> itemController.getItemById(itemId, userId));
    }

    @Test
    void getOwnerItems_WhenNoItems_ShouldReturnEmptyList() {
        when(restClient.get(eq("/items"), eq(userId), eq(ItemResponseDto[].class)))
                .thenReturn(new ItemResponseDto[0]);

        List<ItemResponseDto> response = itemController.getOwnerItems(userId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void search_WithEmptyText_ShouldReturnEmptyList() {
        when(restClient.get(eq("/items/search?text={text}"), eq(userId), eq(ItemDto[].class), eq("")))
                .thenReturn(new ItemDto[0]);

        List<ItemDto> response = itemController.search("", userId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void updateItem_WithPartialUpdate_ShouldSucceed() {
        ItemUpdateDto partialUpdate = new ItemUpdateDto();
        partialUpdate.setName("New name");

        when(restClient.patch(anyString(), any(), anyLong(), any(), anyLong()))
                .thenReturn(itemDto);

        ItemDto response = itemController.updateItem(partialUpdate, userId, itemId);
        assertNotNull(response);
    }

}