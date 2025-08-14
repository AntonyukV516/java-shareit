package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final Long itemId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @Transactional
    void createRequest_ShouldCreateNewRequest() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto("Нужна дрель");
        User requester = new User(userId, "user@email.com", "User");
        ItemRequest newRequest = new ItemRequest(requestId, "Нужна дрель", requester, now);

        when(userService.getUserById(userId)).thenReturn(new UserDto(userId, "User", "user@email.com"));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(newRequest);

        ItemRequestDto result = itemRequestService.createRequest(requestDto, userId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals("Нужна дрель", result.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void getUserRequests_ShouldReturnRequestsWithItems() {
        User requester = new User(userId, "user@email.com", "User");
        ItemRequest request = new ItemRequest(requestId, "Нужна дрель", requester, now);
        Item item = new Item(itemId, "Дрель", "Аккумуляторная дрель", true, new User(), request);

        when(userService.getUserById(userId)).thenReturn(new UserDto());
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(userId);

        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getItems().size());
        assertEquals("Дрель", result.getFirst().getItems().getFirst().getName());
    }

    @Test
    void getAllRequests_ShouldReturnOthersRequests() {
        User otherUser = new User(2L, "other@email.com", "Other");
        ItemRequest request = new ItemRequest(requestId, "Нужен шуруповерт", otherUser, now);
        Item item = new Item(itemId, "Шуруповерт", "Аккумуляторный", true, new User(), request);

        when(userService.getUserById(userId)).thenReturn(new UserDto());
        when(itemRequestRepository.findAllByRequesterIdNot(userId))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getAllRequests(userId);

        assertEquals(1, result.size());
        assertEquals("Нужен шуруповерт", result.getFirst().getDescription());
        assertEquals(1, result.getFirst().getItems().size());
    }

    @Test
    void getRequestById_ShouldReturnRequestWithItems() {
        User requester = new User(userId, "user@email.com", "User");
        ItemRequest request = new ItemRequest(requestId, "Нужна пила", requester, now);
        Item item = new Item(itemId, "Пила", "Электрическая", true, new User(), request);

        when(userService.getUserById(userId)).thenReturn(new UserDto());
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getRequestById(userId, requestId);

        assertNotNull(result);
        assertEquals("Нужна пила", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Пила", result.getItems().getFirst().getName());
    }

    @Test
    void getRequestById_ShouldThrowWhenNotFound() {
        when(userService.getUserById(userId)).thenReturn(new UserDto());
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getRequestById(userId, requestId));

        assertEquals("Запрос с ID " + requestId + " не найден", exception.getMessage());
    }

    @Test
    void addItemsForRequest_ShouldMapItemsCorrectly() {
        User requester = new User(userId, "user@email.com", "User");
        ItemRequest request = new ItemRequest(requestId, "Нужен молоток", requester, now);
        Item item = new Item(itemId, "Молоток", "С гвоздодером", true, new User(), request);

        when(itemRepository.findAllByRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.addItemsForRequest(request);

        assertEquals(1, result.getItems().size());
        assertEquals("Молоток", result.getItems().getFirst().getName());
        assertEquals("С гвоздодером", result.getItems().getFirst().getDescription());
    }
}
