package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          ItemRepository itemRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"));

        if (item.getOwner().getId().equals(bookerId)) {
            throw new IllegalArgumentException("Владелец не может забронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь не доступна для бронирования");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Время начала должно быть раньше времени завершения");
        }

        boolean hasConflicts = bookingRepository.existsApprovedBookingsForItemBetweenDates(
                item.getId(), bookingDto.getStart(), bookingDto.getEnd());

        if (hasConflicts) {
            throw new IllegalArgumentException("Вещь занята в выбраные даты");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Только владелец вещи может одобрить пронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalArgumentException("Бронирование уже в работе");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new SecurityException("Доступ запрещен");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    public List<BookingResponseDto> getUserBookings(Long bookerId, String state) {
        if (!userRepository.existsById(bookerId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        LocalDateTime now = LocalDateTime.now();
        return switch (state.toUpperCase()) {
            case "ALL" -> BookingMapper.ToDtoList(bookingRepository.findByBookerIdOrderByStartDesc(bookerId));
            case "CURRENT" -> BookingMapper.ToDtoList(bookingRepository.findCurrentBookingsByBooker(bookerId, now));
            case "PAST" -> BookingMapper.ToDtoList(bookingRepository.findPastBookingsByBooker(bookerId, now));
            case "FUTURE" -> BookingMapper.ToDtoList(bookingRepository.findFutureBookingsByBooker(bookerId, now));
            case "WAITING" -> BookingMapper.ToDtoList(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    bookerId, BookingStatus.WAITING));
            case "REJECTED" -> BookingMapper.ToDtoList(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    bookerId, BookingStatus.REJECTED));
            default -> throw new IllegalArgumentException("Не известный статус: " + state);
        };
    }

    public List<BookingResponseDto> getOwnerBookings(Long ownerId, String state) {
        if (!userRepository.existsById(ownerId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (state.toUpperCase()) {
            case "ALL" -> BookingMapper.ToDtoList(bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId));
            case "CURRENT" -> BookingMapper.ToDtoList(bookingRepository.findCurrentBookingsByOwner(ownerId, now));
            case "PAST" -> BookingMapper.ToDtoList(bookingRepository.findPastBookingsByOwner(ownerId, now));
            case "FUTURE" -> BookingMapper.ToDtoList(bookingRepository.findFutureBookingsByOwner(ownerId, now));
            case "WAITING" -> BookingMapper.ToDtoList(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId, BookingStatus.WAITING));
            case "REJECTED" -> BookingMapper.ToDtoList(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId, BookingStatus.REJECTED));
            default -> throw new IllegalArgumentException("Не известный статус: " + state);
        };
    }
}