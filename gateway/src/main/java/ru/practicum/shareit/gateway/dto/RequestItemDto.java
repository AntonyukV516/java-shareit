package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestItemDto {
    @NotBlank(message = "Description обязателен", groups = Marker.OnCreate.class)
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}