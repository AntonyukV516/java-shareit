package ru.practicum.shareit.gateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemUpdateDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
