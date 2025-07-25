package ru.practicum.shareit.gateway.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.gateway.RestClientUtils;
import ru.practicum.shareit.gateway.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.gateway.request.dto.ItemRequestDto;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RestClientUtils restClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(
            @RequestBody @Valid CreateItemRequestDto requestDto,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return restClient.post(
                "/requests",
                requestDto,
                userId,
                ItemRequestDto.class
        );
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        ItemRequestDto[] response = restClient.get(
                "/requests",
                userId,
                ItemRequestDto[].class
        );
        return Arrays.asList(response);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        ItemRequestDto[] response = restClient.get(
                "/requests/all",
                userId,
                ItemRequestDto[].class
        );
        return Arrays.asList(response);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(Constants.SHARER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        return restClient.get(
                "/requests/{requestId}",
                userId,
                ItemRequestDto.class,
                requestId
        );
    }
}