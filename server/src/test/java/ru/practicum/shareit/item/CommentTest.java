package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void testEntityStructure() {
        Item item = new Item();
        item.setId(1L);

        User author = new User();
        author.setId(1L);

        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment text");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        assertEquals(1L, comment.getId());
        assertEquals("Test comment text", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(now, comment.getCreated());
    }

    @Test
    void testEqualsAndHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        Comment comment3 = new Comment();
        comment3.setId(2L);

        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
        assertNotEquals(comment1, null);
        assertNotEquals(comment1, new Object());

        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setCreated(now);

        String toStringResult = comment.toString();

        assertTrue(toStringResult.contains("Comment{"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("text='Test comment'"));
        assertTrue(toStringResult.contains("created=" + now));
    }

    @Test
    void testNoArgsConstructor() {
        Comment comment = new Comment();

        assertNull(comment.getId());
        assertNull(comment.getText());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
        assertNull(comment.getCreated());
    }

    @Test
    void testAllArgsConstructor() {
        Item item = new Item();
        User author = new User();
        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment(1L, "Test text", item, author, now);

        assertEquals(1L, comment.getId());
        assertEquals("Test text", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(now, comment.getCreated());
    }
}