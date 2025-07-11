package ru.practicum.shareit.item;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@Data
public class ItemRepositoryImpl {
    private UserRepository userRepository;
    private Map<Long, Item> items = new HashMap<>();
    private long id = 0L;

    @Autowired
    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Long createId() {
        return ++id;
    }

    public Item addItem(Item item, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Сначала добавьте пользователя"));
        item.setId(createId());
        item.setOwner(owner);
        items.put(item.getId(), item);
        log.info("Предмет {} добавлен", item);
        return item;
    }

    public Item updateItem(Item updatedItem, Long userId, Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new EntityNotFoundException("Предмет с id=" + itemId + " не найден");
        }

        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        if (!user.equals(item.getOwner())) {
            throw new RuntimeException("Редактировать может только владелец");
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
        return item;
    }

    public Item getItemById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new EntityNotFoundException("Не найден предмет");
        }
        return item;
    }

    public List<Item> getOwnerItems(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));

        return items.values()
                .stream()
                .filter(i -> i.getOwner().equals(user))
                .toList();
    }

    public List<Item> search(String text) {
        String query = text.trim().toLowerCase();
        return text.isBlank()
                ? List.of()
                : items.values()
                .stream()
                .filter(i -> i.getName().toLowerCase().contains(query)
                        || i.getDescription().toLowerCase().contains(query))
                .filter(Item::getAvailable)
                .toList();
    }
}
