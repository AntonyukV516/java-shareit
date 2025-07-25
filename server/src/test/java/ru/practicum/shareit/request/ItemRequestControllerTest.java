package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final CreateItemRequestDto requestDto = new CreateItemRequestDto("Need item");
    private final ItemRequestDto responseDto = new ItemRequestDto(requestId, "Need item",
            LocalDateTime.now(), List.of());

    @Test
    void createRequest_ShouldReturnCreatedRequest() {
        when(itemRequestService.createRequest(any(), anyLong())).thenReturn(responseDto);

        ItemRequestDto result = itemRequestController.createRequest(requestDto, userId);

        assertEquals(responseDto, result);
        verify(itemRequestService).createRequest(requestDto, userId);
    }

    @Test
    void getUserRequests_ShouldReturnList() {
        when(itemRequestService.getUserRequests(userId)).thenReturn(List.of(responseDto));

        List<ItemRequestDto> result = itemRequestController.getUserRequests(userId);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.getFirst());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests() {
        when(itemRequestService.getAllRequests(userId)).thenReturn(List.of(responseDto));

        List<ItemRequestDto> result = itemRequestController.getAllRequests(userId);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.getFirst());
    }

    @Test
    void getRequestById_ShouldReturnRequest() {
        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(responseDto);

        ItemRequestDto result = itemRequestController.getRequestById(userId, requestId);

        assertEquals(responseDto, result);
    }
}