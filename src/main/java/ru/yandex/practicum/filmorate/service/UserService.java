package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

@Service
public class UserService {
    private static InMemoryUserStorage userStorage = new InMemoryUserStorage();

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public static User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public static User createUser(User user) {
        return userStorage.createUser(user);
    }

    public static User updateUser(User user) {
        return userStorage.updateUser(user);
    }
}
