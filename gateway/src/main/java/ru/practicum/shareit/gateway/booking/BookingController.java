package ru.practicum.shareit.gateway.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.gateway.RestClientUtils;
import ru.practicum.shareit.gateway.booking.dto.BookingRequestDto;
import ru.practicum.shareit.gateway.booking.dto.BookingResponseDto;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final RestClientUtils restClient;

    @PostMapping
    public BookingResponseDto createBooking(
            @Valid @RequestBody BookingRequestDto bookingDto,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {

        log.info("Received booking (MSK): start={}, end={}",
                bookingDto.getStart(), bookingDto.getEnd());

        return restClient.post("/bookings", bookingDto, userId, BookingResponseDto.class);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return restClient.patch(
                "/bookings/{bookingId}?approved={approved}",
                null,
                userId,
                BookingResponseDto.class,
                bookingId,
                approved
        );
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(
            @PathVariable Long bookingId,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return restClient.get(
                "/bookings/{bookingId}",
                userId,
                BookingResponseDto.class,
                bookingId
        );
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        BookingResponseDto[] response = restClient.get(
                "/bookings?state={state}",
                userId,
                BookingResponseDto[].class,
                state
        );
        return Arrays.asList(response);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        BookingResponseDto[] response = restClient.get(
                "/bookings/owner?state={state}",
                userId,
                BookingResponseDto[].class,
                state
        );
        return Arrays.asList(response);
    }
}
