package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.shareit.gateway.RestClientUtils;
import ru.practicum.shareit.gateway.booking.BookingController;
import ru.practicum.shareit.gateway.booking.dto.BookingRequestDto;
import ru.practicum.shareit.gateway.booking.dto.BookingResponseDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private RestClientUtils restClient;

    @InjectMocks
    private BookingController bookingController;

    private final Long userId = 1L;
    private final Long bookingId = 10L;
    private final BookingRequestDto bookingRequestDto = new BookingRequestDto();
    private final BookingResponseDto bookingResponseDto = new BookingResponseDto();

    @Test
    void createBooking_ShouldReturnCreatedBooking() {
        when(restClient.post(anyString(), any(BookingRequestDto.class), anyLong(), eq(BookingResponseDto.class)))
                .thenReturn(bookingResponseDto);

        BookingResponseDto response = bookingController.createBooking(bookingRequestDto, userId);

        assertNotNull(response);
        assertEquals(bookingResponseDto, response);
    }

    @Test
    void approveBooking_ShouldReturnUpdatedBooking() {
        Boolean approved = true;
        when(restClient.patch(anyString(), isNull(), anyLong(), eq(BookingResponseDto.class), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        BookingResponseDto response = bookingController.approveBooking(bookingId, approved, userId);

        assertNotNull(response);
        assertEquals(bookingResponseDto, response);
    }

    @Test
    void getBooking_ShouldReturnBooking() {
        when(restClient.get(anyString(), anyLong(), eq(BookingResponseDto.class), anyLong()))
                .thenReturn(bookingResponseDto);

        BookingResponseDto response = bookingController.getBooking(bookingId, userId);

        assertNotNull(response);
        assertEquals(bookingResponseDto, response);
    }

    @Test
    void getUserBookings_ShouldReturnListOfBookings() {
        String state = "ALL";
        BookingResponseDto[] bookingsArray = {bookingResponseDto};

        when(restClient.get(anyString(), anyLong(), eq(BookingResponseDto[].class), anyString()))
                .thenReturn(bookingsArray);

        List<BookingResponseDto> response = bookingController.getUserBookings(state, userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(bookingResponseDto, response.getFirst());
    }

    @Test
    void getOwnerBookings_ShouldReturnListOfBookings() {
        String state = "ALL";
        BookingResponseDto[] bookingsArray = {bookingResponseDto};

        when(restClient.get(anyString(), anyLong(), eq(BookingResponseDto[].class), anyString()))
                .thenReturn(bookingsArray);

        List<BookingResponseDto> response = bookingController.getOwnerBookings(state, userId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(bookingResponseDto, response.getFirst());
    }

    @Test
    void getUserBookings_WithDefaultState_ShouldUseAll() {
        BookingResponseDto[] bookingsArray = {bookingResponseDto};

        when(restClient.get(anyString(), eq(userId), eq(BookingResponseDto[].class), eq("ALL")))
                .thenReturn(bookingsArray);

        List<BookingResponseDto> response = bookingController.getUserBookings("ALL", userId);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getOwnerBookings_WithCustomState_ShouldPassCorrectState() {
        String state = "PAST";
        BookingResponseDto[] bookingsArray = {bookingResponseDto};

        when(restClient.get(anyString(), eq(userId), eq(BookingResponseDto[].class), eq("PAST")))
                .thenReturn(bookingsArray);

        List<BookingResponseDto> response = bookingController.getOwnerBookings(state, userId);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBooking_WhenNotFound_ShouldThrowException() {
        when(restClient.get(anyString(), anyLong(), eq(BookingResponseDto.class), anyLong()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(HttpClientErrorException.class,
                () -> bookingController.getBooking(bookingId, userId));
    }


    @Test
    void getUserBookings_WhenNoBookings_ShouldReturnEmptyList() {
        when(restClient.get(anyString(), anyLong(), eq(BookingResponseDto[].class), anyString()))
                .thenReturn(new BookingResponseDto[0]);

        List<BookingResponseDto> response = bookingController.getUserBookings("ALL", userId);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}