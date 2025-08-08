package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemBodyDto {

    @NotBlank(message = "Имя не может быть пустым", groups = {Marker.OnCreate.class})
    private String name;

    @NotBlank(message = "Описание не может быть пустым", groups = {Marker.OnCreate.class})
    private String description;

    @NotNull(message = "Cтатус не может быть пустым", groups = {Marker.OnCreate.class})
    private Boolean available;

    private Long requestId;
}