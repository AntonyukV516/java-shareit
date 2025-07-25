package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.gateway.exeption.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class EntityNotFoundExceptionTest {
    @Test
    void shouldCreateAndThrowException() {
        String message = "Test message";
        EntityNotFoundException exception = new EntityNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertThrows(EntityNotFoundException.class, () -> {
            throw new EntityNotFoundException(message);
        });
    }
}