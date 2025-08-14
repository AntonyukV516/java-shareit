package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.controller.UserController;
import ru.practicum.shareit.gateway.dto.RequestUserDto;
import ru.practicum.shareit.gateway.dto.UserDto;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

    }

    @Test
    void addUser() throws Exception {
        RequestUserDto requestUserDto = new RequestUserDto("testName", "test@email.com");
        UserDto userDto = new UserDto(1L, "testName", "test@email.com");

        when(userClient.add(ArgumentMatchers.any())).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.email").value("test@email.com"));

        verify(userClient, times(1)).add(any());
    }

    @Test
    void updateUser() throws Exception {
        long userId = 1L;
        RequestUserDto requestUserDto = new RequestUserDto("updatedName", "updated@email.com");
        UserDto userDto = new UserDto(userId, "updatedName", "updated@email.com");

        when(userClient.updateUser(eq(userId), ArgumentMatchers.any())).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));

        verify(userClient, times(1)).updateUser(eq(userId), any());
    }

    @Test
    void deleteUser() throws Exception {
        long userId = 1L;

        when(userClient.deleteUser(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(userId);
    }

    @Test
    void getUserById() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto(userId, "testName", "test@email.com");
        when(userClient.getUserById(userId)).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.email").value("test@email.com"));

        verify(userClient, times(1)).getUserById(userId);
    }
}