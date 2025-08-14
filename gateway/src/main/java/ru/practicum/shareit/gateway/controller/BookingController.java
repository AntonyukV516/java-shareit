package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.dto.BookingDto;
import ru.practicum.shareit.gateway.dto.RequestBookingDto;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;


    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody RequestBookingDto requestBookingDto,
                                                    @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        log.info("Запрос на создание бронирования: {}, для пользователя с ID {}", requestBookingDto, userId);
        return bookingClient.createBooking(requestBookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId,
                                                     @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        log.info("Запрос на получение бронирования с ID {} для пользователя с ID {}", bookingId, userId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@PathVariable Long bookingId,
                                                     @RequestParam Boolean approved,
                                                     @RequestHeader(Constants.SHARER_USER_ID) Long ownerId) {
        log.info("Запрос на одобрение бронирования с ID {}: {} для владельца с ID {}", bookingId, approved, ownerId);
        return bookingClient.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader(Constants.SHARER_USER_ID) Long userId,
                                                            @RequestParam(defaultValue = "ALL") State state) {
        log.info("Запрос на получение бронирований для пользователя с ID {} в состоянии {}", userId, state);
        return bookingClient.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(@RequestHeader(Constants.SHARER_USER_ID) Long ownerId,
                                                             @RequestParam(defaultValue = "ALL") State state) {
        log.info("Запрос на получение бронирований для владельца с ID {} в состоянии {}", ownerId, state);
        return bookingClient.getOwnerBookings(ownerId, state);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        log.info("Запрос на получение всех бронирований");
        return bookingClient.getAllBookings();
    }
}
