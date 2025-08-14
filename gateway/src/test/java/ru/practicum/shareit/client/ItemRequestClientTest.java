package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.ItemRequestClient;
import ru.practicum.shareit.gateway.dto.RequestDto;
import ru.practicum.shareit.gateway.dto.RequestItemDto;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddItemRequest() {
        RequestItemDto requestItemDto = new RequestItemDto();
        RequestDto requestDto = new RequestDto();
        ResponseEntity<RequestDto> responseEntity = ResponseEntity.ok(requestDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(RequestDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<RequestDto> result = itemRequestClient.addItemRequest(requestItemDto, 1L);

        assertThat(result.getBody()).isEqualTo(requestDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.POST),
                        any(HttpEntity.class), eq(RequestDto.class));
    }

    @Test
    void testGetUserItemRequests() {
        Long userId = 1L;
        List<RequestDto> requests = Collections.singletonList(new RequestDto());
        ResponseEntity<List<RequestDto>> responseEntity = ResponseEntity.ok(requests);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<RequestDto>> result = itemRequestClient.getUserItemRequests(userId);

        assertThat(result.getBody()).containsExactlyElementsOf(requests);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetOtherUsersItemRequests() {
        Long userId = 1L;
        List<RequestDto> requests = Collections.singletonList(new RequestDto());
        ResponseEntity<List<RequestDto>> responseEntity = ResponseEntity.ok(requests);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<RequestDto>> result = itemRequestClient.getOtherUsersItemRequests(userId);

        assertThat(result.getBody()).containsExactlyElementsOf(requests);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void testGetItemRequestById() {
        Long userId = 1L;
        Long requestId = 2L;
        RequestDto requestDto = new RequestDto();
        ResponseEntity<RequestDto> responseEntity = ResponseEntity.ok(requestDto);

        when(restTemplate.exchange(any(String.class),
                eq(HttpMethod.GET), any(HttpEntity.class), eq(RequestDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<RequestDto> result = itemRequestClient.getItemRequestById(userId, requestId);

        assertThat(result.getBody()).isEqualTo(requestDto);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET),
                        any(HttpEntity.class), eq(RequestDto.class));
    }
}
