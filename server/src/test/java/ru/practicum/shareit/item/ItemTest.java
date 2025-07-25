package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testEntityStructure() {
        User owner = new User();
        owner.setId(1L);

        ItemRequest request = new ItemRequest();
        request.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        assertEquals(1L, item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testEqualsAndHashCode() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        Item item3 = new Item();
        item3.setId(2L);

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
        assertNotEquals(item1, null);
        assertNotEquals(item1, new Object());

        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testToString() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        String toStringResult = item.toString();

        assertTrue(toStringResult.contains("Item{"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("name='Test Item'"));
        assertTrue(toStringResult.contains("description='Test Description'"));
        assertTrue(toStringResult.contains("available=true"));
    }

    @Test
    void testNoArgsConstructor() {
        Item item = new Item();

        assertNull(item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getAvailable());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void testAllArgsConstructor() {
        User owner = new User();
        ItemRequest request = new ItemRequest();

        Item item = new Item(1L, "Test Item", "Test Description", true, owner, request);

        assertEquals(1L, item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }
}