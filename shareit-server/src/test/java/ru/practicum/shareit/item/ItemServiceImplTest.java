package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final Long userId = 1L;
    private final Long itemId = 1L;
    private final Long requestId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @Transactional
    void addItem_ShouldCreateItemWithoutRequest() {
        ItemDto itemDto = new ItemDto(null, "Item", "Description", true, null);
        User owner = new User(userId, "owner@email.com", "Owner");
        Item item = new Item(itemId, "Item", "Description", true, owner, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.addItem(itemDto, userId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        verify(itemRepository).save(any());
    }

    @Test
    @Transactional
    void addItem_ShouldCreateItemWithRequest() {
        ItemDto itemDto = new ItemDto(null, "Item", "Description", true, requestId);
        User owner = new User(userId, "owner@email.com", "Owner");
        ItemRequest request = new ItemRequest(requestId, "Description", owner, now);
        Item item = new Item(itemId, "Item", "Description", true, owner, request);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.addItem(itemDto, userId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        verify(itemRepository).save(any());
    }

    @Test
    void addItem_ShouldThrowWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.addItem(new ItemDto(), userId));
    }

    @Test
    @Transactional
    void updateItem_ShouldUpdateName() {
        User owner = new User(userId, "owner@email.com", "Owner");
        Item item = new Item(itemId, "Old", "Desc", true, owner, null);
        ItemUpdateDto update = new ItemUpdateDto(null, "New", null, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.updateItem(update, userId, itemId);

        assertEquals("New", result.getName());
        assertEquals("Desc", result.getDescription());
    }

    @Test
    void updateItem_ShouldThrowWhenNotOwner() {
        User owner = new User(999L, "other@email.com", "Other");
        Item item = new Item(itemId, "Item", "Desc", true, owner, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class,
                () -> itemService.updateItem(new ItemUpdateDto(), userId, itemId));
    }

    @Test
    void getItemById_ShouldReturnItemWithComments() {
        Item item = new Item(itemId, "Item", "Desc", true, new User(), null);
        Comment comment = new Comment(1L, "Text", item, new User(), now);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));

        ItemResponseDto result = itemService.getItemById(itemId);

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
    }

    @Test
    void getOwnerItems_ShouldReturnItemsWithBookingsAndComments() {
        User owner = new User(userId, "owner@email.com", "Owner");
        Item item = new Item(itemId, "Item", "Desc", true, owner, null);
        Booking last = new Booking(1L, now.minusDays(2), now.minusDays(1), item, new User(), BookingStatus.APPROVED);
        Booking next = new Booking(2L, now.plusDays(1), now.plusDays(2), item, new User(), BookingStatus.APPROVED);
        Comment comment = new Comment(1L, "Text", item, new User(), now);

        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));
        when(bookingRepository.findLastBookingsForItems(eq(List.of(itemId)), any(LocalDateTime.class)))
                .thenReturn(List.of(last));
        when(bookingRepository.findNextBookingsForItems(eq(List.of(itemId)), any(LocalDateTime.class)))
                .thenReturn(List.of(next));
        when(commentRepository.findByItemIn(List.of(item))).thenReturn(List.of(comment));

        List<ItemResponseDto> result = itemService.getOwnerItems(userId);

        assertEquals(1, result.size());
        ItemResponseDto dto = result.getFirst();

        assertEquals(1, dto.getComments().size());
        assertEquals(comment.getText(), dto.getComments().getFirst().getText());
    }

    @Test
    void search_ShouldReturnEmptyListForBlankText() {
        List<ItemDto> result = itemService.search(" ");
        assertTrue(result.isEmpty());
    }

    @Test
    void search_ShouldReturnItems() {
        Item item = new Item(itemId, "Item", "Desc", true, new User(), null);
        when(itemRepository.searchByText("test")).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search("test");

        assertEquals(1, result.size());
        assertEquals(itemId, result.getFirst().getId());
    }

    @Test
    @Transactional
    void addComment_ShouldCreateComment() {
        User author = new User(userId, "user@email.com", "User");
        User owner = new User(2L, "owner@email.com", "Owner");
        Item item = new Item(itemId, "Item", "Desc", true, owner, null);
        Comment comment = new Comment(1L, "Text", item, author, now);

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(eq(userId), eq(itemId), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(itemId, userId, "Text");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Text", result.getText());
        assertEquals(author.getName(), result.getAuthorName());

        verify(commentRepository).save(argThat(c ->
                c.getText().equals("Text") &&
                        c.getItem().getId().equals(itemId) &&
                        c.getAuthor().getId().equals(userId)
        ));
    }

    @Test
    void addComment_ShouldThrowWhenNoBookings() {
        User author = new User(userId, "user@email.com", "User");
        Item item = new Item(itemId, "Item", "Desc", true, new User(), null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(eq(userId), eq(itemId), any(LocalDateTime.class)))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.addComment(itemId, userId, "Text"));

        assertEquals("Пользователь не брал эту вещь", exception.getMessage());

        verify(commentRepository, never()).save(any());
    }
}
