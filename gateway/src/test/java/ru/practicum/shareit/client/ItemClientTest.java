package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.ItemClient;
import ru.practicum.shareit.gateway.dto.CommentDto;
import ru.practicum.shareit.gateway.dto.ItemBodyDto;
import ru.practicum.shareit.gateway.dto.ItemDto;
import ru.practicum.shareit.gateway.dto.RequestCommentDto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ItemClient itemClient;

    public ItemClientTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdd() {
        ItemBodyDto itemBodyDto = new ItemBodyDto();
        ItemDto itemDto = new ItemDto();
        ResponseEntity<ItemDto> responseEntity = ResponseEntity.ok(itemDto);

        when(restTemplate.exchange(any(String.class),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(ItemDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<ItemDto> result = itemClient.add(itemBodyDto, 1L);

        assertThat(result.getBody()).isEqualTo(itemDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(ItemDto.class));
    }

    @Test
    void testUpdate() {
        long itemId = 1L;
        ItemBodyDto itemBodyDto = new ItemBodyDto();
        ItemDto itemDto = new ItemDto();
        ResponseEntity<ItemDto> responseEntity = ResponseEntity.ok(itemDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.PATCH),
                any(HttpEntity.class), eq(ItemDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<ItemDto> result = itemClient.update(itemId, itemBodyDto, 1L);

        assertThat(result.getBody()).isEqualTo(itemDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.PATCH),
                        any(HttpEntity.class), eq(ItemDto.class));
    }

    @Test
    void testGetById() {
        long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        ResponseEntity<ItemDto> responseEntity = ResponseEntity.ok(itemDto);

        when(restTemplate.exchange(any(String.class),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(ItemDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<ItemDto> result = itemClient.getById(itemId);

        assertThat(result.getBody()).isEqualTo(itemDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(ItemDto.class));
    }

    @Test
    void testGetItemsByOwner() {
        Long userId = 1L;
        Collection<ItemDto> items = Collections.singletonList(new ItemDto());
        ResponseEntity<Collection<ItemDto>> responseEntity = ResponseEntity.ok(items);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<Collection<ItemDto>> result = itemClient.getItemsByOwner(userId);

        assertThat(result.getBody()).containsExactlyElementsOf(items);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void testSearch() {
        String searchText = "test";
        Long userId = 1L;
        List<ItemDto> items = Collections.singletonList(new ItemDto());
        ResponseEntity<List<ItemDto>> responseEntity = ResponseEntity.ok(items);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<ItemDto>> result = itemClient.search(searchText, userId);

        assertThat(result.getBody()).containsExactlyElementsOf(items);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void testAddComment() {
        Long itemId = 1L;
        RequestCommentDto requestCommentDto = new RequestCommentDto();
        CommentDto commentDto = new CommentDto();
        ResponseEntity<CommentDto> responseEntity = ResponseEntity.ok(commentDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(CommentDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<CommentDto> result = itemClient.addComment(itemId, requestCommentDto, 1L);

        assertThat(result.getBody()).isEqualTo(commentDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class),
                        eq(CommentDto.class));
    }
}
