package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.gateway.booking.BookingStatus;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingStatusTest {
    @Test
    void shouldCoverAllEnumValues() {
        assertNotNull(BookingStatus.WAITING);
        assertNotNull(BookingStatus.APPROVED);
        assertNotNull(BookingStatus.REJECTED);
        assertNotNull(BookingStatus.CANCELLED);
    }
}
