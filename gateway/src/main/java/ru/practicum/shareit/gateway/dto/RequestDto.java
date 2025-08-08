package ru.practicum.shareit.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private Set<ItemDto> items;
}
