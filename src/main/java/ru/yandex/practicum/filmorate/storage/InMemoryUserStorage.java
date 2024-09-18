package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;

@Component
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> users = new HashMap<>();

    public List<User> getAllUsers() {
        List<User> listOfUsers = new ArrayList<>();
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            listOfUsers.add(entry.getValue());
        }
        return listOfUsers;
    }

    public User getUserById(Integer userId) {
        return users.get(userId);
    }

    public User createUser(User user) {
        if (validation(user)) {
            user.setId(getNextId());
            if ((user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) && (user.getLogin() != null && !user.getLogin().isBlank() && !user.getLogin().isEmpty())) {
                user.setName(user.getLogin());
                log.info("Имя для пользователя {} будет равно его логину {}", user.getName(), user.getLogin());
            }
            // сохраняем нового пользователя
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь {}", user.getName());
        }
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователя с таким ID нет");
        }
        if (validation(user)) {
            // обновлем пользователя
            users.put(user.getId(), user);
        }
        return user;
    }

    private boolean validation(User user) {
        boolean result = true;
        if (user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()) {
            result = false;
            log.info("Валидация email не прошла");
            throw new ValidationException("Имейл не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            result = false;
            log.info("Валидация 2 email не прошла");
            throw new ValidationException("Неверный формат почтового адреса");
        }
        if (user.getLogin().isEmpty()) {
            result = false;
            log.info("Валидация логина не прошла");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            result = false;
            log.info("Валидация логина на пробелы не прошла");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            result = false;
            log.info("Валидация на дату рождения не прошла");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        return result;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
