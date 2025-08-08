package ru.practicum.shareit.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.controller.State;
import ru.practicum.shareit.gateway.dto.BookingDto;
import ru.practicum.shareit.gateway.dto.RequestBookingDto;
import ru.practicum.shareit.gateway.dto.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookingClient bookingClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBooking() {
        RequestBookingDto requestDto = new RequestBookingDto(
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);
        BookingDto bookingDto = new BookingDto(
                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null,
                null, BookingStatus.APPROVED);
        ResponseEntity<BookingDto> responseEntity = ResponseEntity.ok(bookingDto);

        when(restTemplate.exchange(any(String.class),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(BookingDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<BookingDto> result = bookingClient.createBooking(requestDto, 1L);

        assertThat(result.getBody()).isEqualTo(bookingDto);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.POST),
                        any(HttpEntity.class), eq(BookingDto.class));
    }

    @Test
    public void testGetBookingById() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, null, BookingStatus.APPROVED);
        ResponseEntity<BookingDto> responseEntity = ResponseEntity.ok(bookingDto);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(BookingDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<BookingDto> result = bookingClient.getBookingById(1L, 1L);

        assertThat(result.getBody()).isEqualTo(bookingDto);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET),
                        any(HttpEntity.class), eq(BookingDto.class));
    }

    @Test
    public void testApproveBooking() {
        BookingDto bookingDto = new BookingDto(
                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                null, null, BookingStatus.APPROVED);
        ResponseEntity<BookingDto> responseEntity = ResponseEntity.ok(bookingDto);

        when(restTemplate.exchange(any(String.class),
                eq(HttpMethod.PATCH), any(HttpEntity.class), eq(BookingDto.class)))
                .thenReturn(responseEntity);

        ResponseEntity<BookingDto> result = bookingClient.approveBooking(1L, true, 1L);

        assertThat(result.getBody()).isEqualTo(bookingDto);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(BookingDto.class));
    }

    @Test
    public void testGetUserBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, null, BookingStatus.APPROVED);
        List<BookingDto> bookings = Collections.singletonList(bookingDto);
        ResponseEntity<List<BookingDto>> responseEntity = ResponseEntity.ok(bookings);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<BookingDto>> result = bookingClient.getUserBookings(1L, State.ALL);

        assertThat(result.getBody()).containsExactly(bookingDto);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);

        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    public void testGetOwnerBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, null, BookingStatus.APPROVED);
        List<BookingDto> bookings = Collections.singletonList(bookingDto);
        ResponseEntity<List<BookingDto>> responseEntity = ResponseEntity.ok(bookings);

        when(restTemplate.exchange(any(String.class),
                eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<BookingDto>> result = bookingClient.getOwnerBookings(1L, State.ALL);

        assertThat(result.getBody()).containsExactly(bookingDto);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class),
                        any(ParameterizedTypeReference.class));
    }

    @Test
    public void testGetAllBookings() {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, null, BookingStatus.APPROVED);
        List<BookingDto> bookings = Collections.singletonList(bookingDto);
        ResponseEntity<List<BookingDto>> responseEntity = ResponseEntity.ok(bookings);

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET),
                any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ResponseEntity<List<BookingDto>> result = bookingClient.getAllBookings();

        assertThat(result.getBody()).containsExactly(bookingDto);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET),
                        any(HttpEntity.class), any(ParameterizedTypeReference.class));
    }
}
