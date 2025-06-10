package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NonNull
    private Boolean available;
    private User owner;
}
