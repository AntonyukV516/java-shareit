package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private final Long userId = 1L;
    private final Long ownerId = 2L;
    private final Long bookingId = 1L;
    private final Long itemId = 1L;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime start = now.plusDays(1);
    private final LocalDateTime end = now.plusDays(2);

    @Test
    @Transactional
    void createBooking_ShouldSuccess() {
        BookingRequestDto request = new BookingRequestDto(itemId, start, end);
        User booker = new User(userId, "booker@email.com", "Booker");
        Item item = new Item(itemId, "Item", "Description", true,
                new User(ownerId, "owner@email.com", "Owner"), null);
        Booking savedBooking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsApprovedBookingsForItemBetweenDates(itemId, start, end)).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(savedBooking);

        BookingResponseDto result = bookingService.createBooking(request, userId);

        assertNotNull(result);
        assertEquals(bookingId, result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
    }

    @Test
    void createBooking_ShouldThrowWhenUserNotFound() {
        BookingRequestDto request = new BookingRequestDto(itemId, start, end);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(request, userId));
    }

    @Test
    void createBooking_ShouldThrowWhenItemNotFound() {
        BookingRequestDto request = new BookingRequestDto(itemId, start, end);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(request, userId));
    }

    @Test
    void createBooking_ShouldThrowWhenOwnerBooksOwnItem() {
        BookingRequestDto request = new BookingRequestDto(itemId, start, end);
        User owner = new User(userId, "owner@email.com", "Owner");
        Item item = new Item(itemId, "Item", "Description", true, owner, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(request, userId));
    }

    @Test
    void createBooking_ShouldThrowWhenItemNotAvailable() {
        BookingRequestDto request = new BookingRequestDto(itemId, start, end);
        User booker = new User(userId, "booker@email.com", "Booker");
        Item item = new Item(itemId, "Item", "Description", false,
                new User(ownerId, "owner@email.com", "Owner"), null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(request, userId));
    }

    @Test
    void createBooking_ShouldThrowWhenInvalidDates() {
        BookingRequestDto request = new BookingRequestDto(itemId, end, start);
        User user = new User(userId, "user@email.com", "User");
        Item item = new Item(itemId, "Item", "Description", true, new User(ownerId, "owner@email.com", "Owner"), null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(request, userId));

        assertEquals("Время начала должно быть раньше времени завершения", exception.getMessage());
    }

    @Test
    void createBooking_ShouldThrowWhenDatesConflict() {
        BookingRequestDto request = new BookingRequestDto(itemId, start, end);
        User user = new User(userId, "user@email.com", "User");
        Item item = new Item(itemId, "Item", "Description", true, new User(ownerId, "owner@email.com", "Owner"), null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsApprovedBookingsForItemBetweenDates(itemId, start, end))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(request, userId));

        assertEquals("Вещь занята в выбраные даты", exception.getMessage());
    }

    @Test
    void approveBooking_ShouldApprove() {
        User owner = new User(ownerId, "owner@email.com", "Owner");
        User booker = new User(userId, "booker@email.com", "Booker");
        Item item = new Item(itemId, "Item", "Description", true, owner, null);

        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDto result = bookingService.approveBooking(bookingId, ownerId, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approveBooking_ShouldReject() {
        User owner = new User(ownerId, "owner@email.com", "Owner");
        User booker = new User(userId, "booker@email.com", "Booker");
        Item item = new Item(itemId, "Item", "Description", true, owner, null);

        Booking booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDto result = bookingService.approveBooking(bookingId, ownerId, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void approveBooking_ShouldThrowWhenBookingNotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(bookingId, ownerId, true));
    }

    @Test
    void approveBooking_ShouldThrowWhenNotOwner() {
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.WAITING)
                .item(new Item(itemId, "Item", "Desc", true, new User(999L, "other", "Other"), null))
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(SecurityException.class,
                () -> bookingService.approveBooking(bookingId, ownerId, true));
    }

    @Test
    void approveBooking_ShouldThrowWhenNotWaiting() {
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.APPROVED)
                .item(new Item(itemId, "Item", "Desc", true, new User(ownerId, "owner", "Owner"), null))
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.approveBooking(bookingId, ownerId, true));
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(new User(userId, "booker", "Booker"))
                .item(new Item(itemId, "Item", "Desc", true, new User(ownerId, "owner", "Owner"), null))
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getBookingById(bookingId, userId);

        assertNotNull(result);
    }

    @Test
    void getBookingById_ShouldThrowWhenNotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(bookingId, userId));
    }

    @Test
    void getBookingById_ShouldThrowWhenNoAccess() {
        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(new User(999L, "other", "Other"))
                .item(new Item(itemId, "Item", "Desc", true, new User(888L, "another", "Another"), null))
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(SecurityException.class,
                () -> bookingService.getBookingById(bookingId, userId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void getUserBookings_ShouldReturnForEachState(String state) {
        User booker = new User(userId, "booker@email.com", "Booker");
        Item item = new Item(itemId, "Item", "Description", true,
                new User(ownerId, "owner@email.com", "Owner"), null);
        Booking booking = new Booking(bookingId, start, end, item, booker, BookingStatus.WAITING);

        LocalDateTime fixedNow = LocalDateTime.now();
        when(userRepository.existsById(userId)).thenReturn(true);

        switch (state) {
            case "ALL":
                doReturn(List.of(booking)).when(bookingRepository).findByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findCurrentBookingsByBooker(eq(userId), any(LocalDateTime.class));
                break;
            case "PAST":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findPastBookingsByBooker(eq(userId), any(LocalDateTime.class));
                break;
            case "FUTURE":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findFutureBookingsByBooker(eq(userId), any(LocalDateTime.class));
                break;
            case "WAITING":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
        }

        List<BookingResponseDto> result = bookingService.getUserBookings(userId, state);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getUserBookings_ShouldThrowWhenUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getUserBookings(userId, "ALL"));
    }

    @Test
    void getUserBookings_ShouldThrowWhenInvalidState() {
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getUserBookings(userId, "INVALID"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"})
    void getOwnerBookings_ShouldReturnForEachState(String state) {
        User owner = new User(ownerId, "owner@email.com", "Owner");
        Item item = new Item(itemId, "Item", "Description", true, owner, null);
        Booking booking = new Booking(bookingId, start, end, item,
                new User(userId, "booker@email.com", "Booker"),
                BookingStatus.WAITING);

        LocalDateTime fixedNow = LocalDateTime.now(); // Фиксируем время для теста
        when(userRepository.existsById(ownerId)).thenReturn(true);

        switch (state) {
            case "ALL":
                doReturn(List.of(booking)).when(bookingRepository).findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "CURRENT":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findCurrentBookingsByOwner(eq(ownerId), any(LocalDateTime.class));
                break;
            case "PAST":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findPastBookingsByOwner(eq(ownerId), any(LocalDateTime.class));
                break;
            case "FUTURE":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findFutureBookingsByOwner(eq(ownerId), any(LocalDateTime.class));
                break;
            case "WAITING":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                doReturn(List.of(booking)).when(bookingRepository)
                        .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
        }

        List<BookingResponseDto> result = bookingService.getOwnerBookings(ownerId, state);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void getOwnerBookings_ShouldThrowWhenUserNotFound() {
        when(userRepository.existsById(ownerId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getOwnerBookings(ownerId, "ALL"));
    }

    @Test
    void getOwnerBookings_ShouldThrowWhenInvalidState() {
        when(userRepository.existsById(ownerId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getOwnerBookings(ownerId, "INVALID"));
    }
}