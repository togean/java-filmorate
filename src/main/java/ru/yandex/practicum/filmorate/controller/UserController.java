package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;

@RestController
@RequestMapping("/users")
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();
    @GetMapping
    public Collection<User> getAllFilms(){
        return users.values();
    }

    @PostMapping
    public User createFilm(@RequestBody User user){
        if(validation(user)){
            user.setId(getNextId());
            // сохраняем нового пользователя
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь {}", user.getName());
        }
        return user;
    }

    @PutMapping
    public User updateFilm(@RequestBody User user){
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if(validation(user)) {
            // обновлем пользователя
            users.put(user.getId(), user);
        }
        return user;
    }

    public boolean validation(User user){
        boolean result = true;
        if(user.getEmail().isEmpty()){
            result = false;
            log.info("Валидация email не прошла");
            throw new ValidationException("Имейл не может быть пустым");
        }
        if(!user.getEmail().contains("@")){
            result = false;
            log.info("Валидация 2 email не прошла");
            throw new ValidationException("Неверный формат почтового адреса");
        }
        if(user.getLogin().isEmpty()){
            result = false;
            log.info("Валидация логина не прошла");
            throw new ValidationException("Логин не может быть пустым");
        }
        if(user.getLogin().contains(" ")){
            result = false;
            log.info("Валидация логина на пробелы не прошла");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if(user.getBirthday().isAfter(LocalDate.now())){
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
