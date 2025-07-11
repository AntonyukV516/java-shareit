package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден с id " + userId));
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto updatedItem, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Предмет не найден с id " + itemId));
        if (!item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Пользователь не владелец");
        }

        if (updatedItem.getName() != null) {
            item.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            item.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            item.setAvailable(updatedItem.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemResponseDto getItemById(Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Предмет не найден с id " + itemId));

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        return ItemMapper.toItemResponseDto(item, null, null, comments);
    }

    @Override
    public List<ItemResponseDto> getOwnerItems(long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findByOwnerId(userId);

        Map<Long, Booking> lastBookings = bookingRepository
                .findLastBookingsForItems(items.stream().map(Item::getId).toList(), now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        Function.identity()
                ));

        Map<Long, Booking> nextBookings = bookingRepository
                .findNextBookingsForItems(items.stream().map(Item::getId).toList(), now)
                .stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        Function.identity()
                ));

        Map<Long, List<CommentDto>> commentsByItem = commentRepository.findByItemIn(items).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));

        return items.stream()
                .map(item -> {
                    Booking last = lastBookings.get(item.getId());
                    Booking next = nextBookings.get(item.getId());
                    List<CommentDto> comments = commentsByItem.getOrDefault(item.getId(), List.of());
                    return ItemMapper.toItemResponseDto(item, last, next, comments);
                })
                .toList();
    }


    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository
                .searchByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, String text) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден с id " + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет не найден с id " + itemId));

        boolean hasBookings = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now());

        if (!hasBookings) {
            throw new IllegalArgumentException("Пользователь не брал эту вещь");
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }
}

