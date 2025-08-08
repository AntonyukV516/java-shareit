package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCommentDto {
    @NotBlank(message = "Текст комментария должен быть указан")
    private String text;
}
