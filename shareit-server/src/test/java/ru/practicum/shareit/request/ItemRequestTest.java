package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void testEntityStructure() {
        User requester = new User();
        requester.setId(1L);
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Test description");
        request.setRequester(requester);
        request.setCreated(created);

        assertEquals(1L, request.getId());
        assertEquals("Test description", request.getDescription());
        assertEquals(requester, request.getRequester());
        assertEquals(created, request.getCreated());
    }

    @Test
    void testEqualsAndHashCode() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        ItemRequest request3 = new ItemRequest();
        request3.setId(2L);

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime created = LocalDateTime.now();
        User requester = new User();
        requester.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Test description");
        request.setRequester(requester);
        request.setCreated(created);

        String toStringResult = request.toString();

        assertTrue(toStringResult.contains("ItemRequest{"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("description='Test description'"));
        assertTrue(toStringResult.contains("requester=1"));
        assertTrue(toStringResult.contains("created=" + created));
    }

    @Test
    void testNoArgsConstructor() {
        ItemRequest request = new ItemRequest();

        assertNull(request.getId());
        assertNull(request.getDescription());
        assertNull(request.getRequester());
        assertNull(request.getCreated());
    }

    @Test
    void testAllArgsConstructor() {
        User requester = new User();
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request = new ItemRequest(1L, "Test description", requester, created);

        assertEquals(1L, request.getId());
        assertEquals("Test description", request.getDescription());
        assertEquals(requester, request.getRequester());
        assertEquals(created, request.getCreated());
    }

    @Test
    void testToStringWithNullRequester() {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Test description");
        request.setCreated(created);

        String toStringResult = request.toString();

        assertTrue(toStringResult.contains("requester=null"));
    }
}
