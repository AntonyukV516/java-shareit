package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUserDto {

    @NotBlank(message = "Name обязателен", groups = Marker.OnCreate.class)
    private String name;

    @Email(message = "Ошибка в email", groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(message = "Email обязателен", groups = Marker.OnCreate.class)
    private String email;
}