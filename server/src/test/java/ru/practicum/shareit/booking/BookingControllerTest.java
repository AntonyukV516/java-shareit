package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final BookingRequestDto bookingRequestDto = new BookingRequestDto();
    private final BookingResponseDto bookingResponseDto = new BookingResponseDto();

    @Test
    void createBooking_ShouldReturnCreatedBooking() {
        when(bookingService.createBooking(any(BookingRequestDto.class), anyLong()))
                .thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingController.createBooking(bookingRequestDto, userId);

        assertNotNull(result);
        assertEquals(bookingResponseDto, result);
        verify(bookingService).createBooking(bookingRequestDto, userId);
    }

    @Test
    void approveBooking_ShouldReturnApprovedBooking() {
        boolean approved = true;
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingController.approveBooking(bookingId, approved, userId);

        assertNotNull(result);
        assertEquals(bookingResponseDto, result);
        verify(bookingService).approveBooking(bookingId, userId, approved);
    }

    @Test
    void getBooking_ShouldReturnBooking() {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingController.getBooking(bookingId, userId);

        assertNotNull(result);
        assertEquals(bookingResponseDto, result);
        verify(bookingService).getBookingById(bookingId, userId);
    }

    @Test
    void getBooking_ShouldThrowWhenNotFound() {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class,
                () -> bookingController.getBooking(bookingId, userId));
    }

    @Test
    void getUserBookings_ShouldReturnList() {
        String state = "ALL";
        when(bookingService.getUserBookings(anyLong(), anyString()))
                .thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> result = bookingController.getUserBookings(state, userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingResponseDto, result.getFirst());
        verify(bookingService).getUserBookings(userId, state);
    }

    @Test
    void getOwnerBookings_ShouldReturnList() {
        String state = "FUTURE";
        when(bookingService.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> result = bookingController.getOwnerBookings(state, userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingResponseDto, result.getFirst());
        verify(bookingService).getOwnerBookings(userId, state);
    }

    @Test
    void getUserBookings_ShouldUseDefaultState() {
        when(bookingService.getUserBookings(eq(userId), eq("ALL")))
                .thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> result = bookingController.getUserBookings("ALL", userId);

        assertNotNull(result);
        verify(bookingService).getUserBookings(userId, "ALL");
    }

    @Test
    void getOwnerBookings_ShouldUseDefaultState() {
        when(bookingService.getOwnerBookings(eq(userId), eq("ALL")))
                .thenReturn(List.of(bookingResponseDto));

        List<BookingResponseDto> result = bookingController.getOwnerBookings("ALL", userId);

        assertNotNull(result);
        verify(bookingService).getOwnerBookings(userId, "ALL");
    }
}
