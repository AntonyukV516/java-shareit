package ru.practicum.shareit.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@Data
public class UserRepositoryImpl {
    private Map<Long, User> users = new HashMap<>();
    private long id = 0L;

    private Long createId() {
        return ++id;
    }

    public User addUser(User user) {
        if (isEmailExist(user.getEmail())) {
            throw new RuntimeException("Email уже существует");
        }
        user.setId(createId());
        users.put(user.getId(), user);
        log.info("Пользователь {} добавлен", user);
        return user;
    }

    public User deleteUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new EntityNotFoundException("не найден пользователь");
        }
        users.remove(id);
        log.info("Пользователь {} удален", user);
        return user;
    }

    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new EntityNotFoundException("пользователь с id=" + id + " не найден");
        }
        log.info("Пользователь {} найден", user);
        return user;
    }

    public List<User> getUsers() {
        return users.values().stream().toList();
    }

    public User updateUser(Long id, User updatedUser) {
        User user = users.get(id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(user.getEmail())) {
            if (isEmailExist(updatedUser.getEmail())) {
                throw new RuntimeException("Email уже занят");
            }
        }

        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        log.info("пользователь {} обновлен", user);
        return user;
    }

    private boolean isEmailExist(String email) {
        List<String> listEmail = new ArrayList<>(users.values()
                .stream()
                .map(User::getEmail)
                .toList());
        return listEmail.contains(email);
    }
}
