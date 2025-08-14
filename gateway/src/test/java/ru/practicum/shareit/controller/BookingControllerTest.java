package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.controller.BookingController;
import ru.practicum.shareit.gateway.controller.State;
import ru.practicum.shareit.gateway.dto.BookingDto;
import ru.practicum.shareit.gateway.dto.ItemDto;
import ru.practicum.shareit.gateway.dto.RequestBookingDto;
import ru.practicum.shareit.gateway.dto.BookingStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


public class BookingControllerTest {
    private ItemDto item;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController bookingController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        item = new ItemDto(1L, "name", "test", true, null, null, null, null);
    }


    @Test
    void createBooking() throws Exception {
        RequestBookingDto requestBookingDto = new RequestBookingDto(LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(10), item.getId());
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(10), item, null, BookingStatus.WAITING);

        when(bookingClient.createBooking(any(), anyLong())).thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBookingDto))
                        .header(Constants.SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookingClient, times(1)).createBooking(any(), anyLong());
    }

    @Test
    void getBookingById() throws Exception {
        Long bookingId = 1L;
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(10), item, null, BookingStatus.WAITING);

        when(bookingClient.getBookingById(bookingId, 1L)).thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Constants.SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookingClient, times(1)).getBookingById(bookingId, 1L);
    }

    @Test
    void approveBooking() throws Exception {
        Long bookingId = 1L;

        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(10), item, null, BookingStatus.WAITING);

        when(bookingClient.approveBooking(bookingId, true, 1L))
                .thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header(Constants.SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookingClient, times(1))
                .approveBooking(bookingId, true, 1L);
    }

    @Test
    void getUserBookings() throws Exception {
        List<BookingDto> bookings = Collections.singletonList(new BookingDto(/* инициализация */));

        when(bookingClient.getUserBookings(1L, State.ALL)).thenReturn(ResponseEntity.ok(bookings));

        mockMvc.perform(get("/bookings")
                        .header(Constants.SHARER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookingClient, times(1)).getUserBookings(1L, State.ALL);
    }

    @Test
    void getOwnerBookings() throws Exception {
        List<BookingDto> bookings = Collections.singletonList(new BookingDto(1L,
                LocalDateTime.now(), LocalDateTime.now().plusSeconds(10), item,
                null, BookingStatus.WAITING));

        when(bookingClient.getOwnerBookings(1L, State.ALL))
                .thenReturn(ResponseEntity.ok(bookings));

        mockMvc.perform(get("/bookings/owner")
                        .header(Constants.SHARER_USER_ID, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookingClient, times(1)).getOwnerBookings(1L, State.ALL);
    }

    @Test
    void getAllBookings() throws Exception {
        List<BookingDto> bookings = Collections.singletonList(new BookingDto(1L,
                LocalDateTime.now(), LocalDateTime.now().plusSeconds(10),
                item, null, BookingStatus.WAITING));

        when(bookingClient.getAllBookings()).thenReturn(ResponseEntity.ok(bookings));

        mockMvc.perform(get("/bookings/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookingClient, times(1)).getAllBookings();
    }
}
