package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.gateway.RestClientUtils;
import ru.practicum.shareit.gateway.request.ItemRequestController;
import ru.practicum.shareit.gateway.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private RestClientUtils restClient;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final Long userId = 1L;
    private final Long requestId = 10L;
    private final CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto();
    private final ItemRequestDto itemRequestDto = new ItemRequestDto();

    @Test
    void createRequest_ShouldReturnCreatedRequest() {
        when(restClient.post(
                eq("/requests"),
                any(CreateItemRequestDto.class),
                eq(userId),
                eq(ItemRequestDto.class))
        ).thenReturn(itemRequestDto);

        ItemRequestDto response = itemRequestController.createRequest(createItemRequestDto, userId);

        assertNotNull(response);
        assertEquals(itemRequestDto, response);
    }

    @Test
    void getUserRequests_ShouldReturnListOfRequests() {
        ItemRequestDto[] requestsArray = {itemRequestDto};
        when(restClient.get(
                eq("/requests"),
                eq(userId),
                eq(ItemRequestDto[].class))
        ).thenReturn(requestsArray);

        List<ItemRequestDto> response = itemRequestController.getUserRequests(userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(itemRequestDto, response.getFirst());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests() {
        ItemRequestDto[] requestsArray = {itemRequestDto, itemRequestDto};
        when(restClient.get(
                eq("/requests/all"),
                eq(userId),
                eq(ItemRequestDto[].class))
        ).thenReturn(requestsArray);

        List<ItemRequestDto> response = itemRequestController.getAllRequests(userId);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void getRequestById_ShouldReturnSpecificRequest() {
        when(restClient.get(
                eq("/requests/{requestId}"),
                eq(userId),
                eq(ItemRequestDto.class),
                eq(requestId))
        ).thenReturn(itemRequestDto);

        ItemRequestDto response = itemRequestController.getRequestById(userId, requestId);

        assertNotNull(response);
        assertEquals(itemRequestDto, response);
    }

    @Test
    void getRequestById_WhenNotFound_ShouldThrowException() {
        when(restClient.get(
                eq("/requests/{requestId}"),
                eq(userId),
                eq(ItemRequestDto.class),
                eq(requestId))
        ).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(HttpClientErrorException.class,
                () -> itemRequestController.getRequestById(userId, requestId));
    }

    @Test
    void getUserRequests_WhenNoRequests_ShouldReturnEmptyList() {
        when(restClient.get(
                eq("/requests"),
                eq(userId),
                eq(ItemRequestDto[].class))
        ).thenReturn(new ItemRequestDto[0]);

        List<ItemRequestDto> response = itemRequestController.getUserRequests(userId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getAllRequests_WhenNoRequests_ShouldReturnEmptyList() {
        when(restClient.get(
                eq("/requests/all"),
                eq(userId),
                eq(ItemRequestDto[].class))
        ).thenReturn(new ItemRequestDto[0]);

        List<ItemRequestDto> response = itemRequestController.getAllRequests(userId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }


}