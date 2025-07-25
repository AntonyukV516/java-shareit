package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exeption.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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