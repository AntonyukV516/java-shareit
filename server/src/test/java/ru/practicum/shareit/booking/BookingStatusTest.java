package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

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
