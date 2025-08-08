package ru.practicum.shareit.gateway.client;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.controller.State;
import ru.practicum.shareit.gateway.dto.BookingDto;
import ru.practicum.shareit.gateway.dto.RequestBookingDto;

import java.util.List;


@Slf4j
@Service
public class BookingClient extends BaseClient {
    private static final String BOOKING_PREFIX = "/bookings";

    protected BookingClient(RestTemplate restTemplate,
                            @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<BookingDto> createBooking(RequestBookingDto requestBookingDto, Long userId) {
        String url = serverUrl + BOOKING_PREFIX;
        return sendRequest(url, HttpMethod.POST, requestBookingDto, BookingDto.class, userId);
    }

    public ResponseEntity<BookingDto> getBookingById(Long bookingId, Long userId) {
        String url = serverUrl + BOOKING_PREFIX + "/" + bookingId;
        return sendRequest(url, HttpMethod.GET, null, BookingDto.class, userId);
    }

    public ResponseEntity<BookingDto> approveBooking(Long bookingId, Boolean approved, Long ownerId) {
        String url = serverUrl + BOOKING_PREFIX + "/" + bookingId + "?approved=" + approved;
        return sendRequest(url, HttpMethod.PATCH, null, BookingDto.class, ownerId);
    }

    public ResponseEntity<List<BookingDto>> getUserBookings(Long userId, State state) {
        String url = serverUrl + BOOKING_PREFIX + "?state=" + state;
        return sendRequest(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BookingDto>>() {}, userId);
    }

    public ResponseEntity<List<BookingDto>> getOwnerBookings(Long ownerId, State state) {
        String url = serverUrl + BOOKING_PREFIX + "/owner?state=" + state;
        return sendRequest(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BookingDto>>() {}, ownerId);
    }

    public ResponseEntity<List<BookingDto>> getAllBookings() {
        String url = serverUrl + BOOKING_PREFIX + "/all";
        return sendRequest(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<BookingDto>>() {}, null);
    }
}
