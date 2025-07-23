package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto createRequest(CreateItemRequestDto requestDto, Long userId) {
        User requester = UserMapper.toUser(userService.getUserById(userId));
        ItemRequest newRequest = ItemRequestMapper.toItemRequest(requestDto, requester);
        ItemRequest savedRequest = itemRequestRepository.save(newRequest);
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    public List<ItemRequestDto> getUserRequests(Long userId) {
        userService.getUserById(userId);
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(this::addItemsForRequest)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(Long userId) {
        userService.getUserById(userId);
        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequesterIdNot(userId);
        return requests.stream()
                .map(this::addItemsForRequest)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Запрос с ID " + requestId + " не найден"));
        return addItemsForRequest(request);
    }

    private ItemRequestDto addItemsForRequest(ItemRequest request) {
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
        List<Item> items = itemRepository.findAllByRequestId(request.getId());
        dto.setItems(items.stream()
                .map(ItemRequestMapper::toItemForRequestDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
