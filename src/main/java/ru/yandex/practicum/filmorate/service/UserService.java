package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
public interface UserService {
    Collection<User> getAllUsers();

    User getUserById(Integer userId);

    User createUser(User user);

    User updateUser(User user);
}
