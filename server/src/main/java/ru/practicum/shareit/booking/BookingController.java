package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(
            @RequestBody BookingRequestDto bookingDto,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {

        LocalDateTime startUtc = bookingDto.getStart().minusHours(3);
        LocalDateTime endUtc = bookingDto.getEnd().minusHours(3);

        BookingRequestDto correctedDto = new BookingRequestDto(
                bookingDto.getItemId(),
                startUtc,
                endUtc
        );
        log.debug("Creating booking with start={}, end={}", bookingDto.getStart(), bookingDto.getEnd());
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(
            @PathVariable Long bookingId,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(Constants.SHARER_USER_ID) Long userId) {
        return bookingService.getOwnerBookings(userId, state);
    }
}
