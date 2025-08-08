package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.gateway.client.ItemRequestClient;
import ru.practicum.shareit.gateway.controller.ItemRequestController;
import ru.practicum.shareit.gateway.dto.RequestDto;
import ru.practicum.shareit.gateway.dto.RequestItemDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemRequestControllerTest {

    @Mock
    private ItemRequestClient itemRequestClient;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void addItemRequest() throws Exception {
        RequestItemDto requestItemDto = new RequestItemDto("test", LocalDateTime.now());
        RequestDto requestDto = new RequestDto(1L, "test",
                null, LocalDateTime.now(), new HashSet<>());

        when(itemRequestClient.addItemRequest(ArgumentMatchers.any(),
                anyLong())).thenReturn(ResponseEntity.ok(requestDto));

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestItemDto))
                        .header(Constants.SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("test"));

        verify(itemRequestClient, times(1)).addItemRequest(any(), anyLong());
    }

    @Test
    void getUserItemRequests() throws Exception {
        long userId = 1L;
        RequestDto requestDto = new RequestDto(1L, "test", null, LocalDateTime.now(), new HashSet<>());

        when(itemRequestClient.getUserItemRequests(userId))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(requestDto)));

        mockMvc.perform(get("/requests")
                        .header(Constants.SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("test"));

        verify(itemRequestClient, times(1)).getUserItemRequests(userId);
    }

    @Test
    void getOtherUsersItemRequests() throws Exception {
        long userId = 1L;
        RequestDto requestDto = new RequestDto(1L, "test",
                null, LocalDateTime.now(), new HashSet<>());

        when(itemRequestClient.getOtherUsersItemRequests(userId))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(requestDto)));

        mockMvc.perform(get("/requests/all")
                        .header(Constants.SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("test"));

        verify(itemRequestClient, times(1)).getOtherUsersItemRequests(userId);
    }

    @Test
    void getItemRequestById() throws Exception {
        long userId = 1L;
        long requestId = 1L;
        RequestDto requestDto = new RequestDto(requestId, "test",
                null, LocalDateTime.now(), new HashSet<>());

        when(itemRequestClient.getItemRequestById(userId, requestId))
                .thenReturn(ResponseEntity.ok(requestDto));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(Constants.SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("test"));

        verify(itemRequestClient, times(1)).getItemRequestById(userId, requestId);
    }
}
