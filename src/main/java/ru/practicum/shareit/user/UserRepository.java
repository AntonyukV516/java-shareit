package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User deleteUser(Long id);

    User getUserById(Long id);

    List<User> getUsers();

    User updateUser(Long id, User updatedUser);
}
