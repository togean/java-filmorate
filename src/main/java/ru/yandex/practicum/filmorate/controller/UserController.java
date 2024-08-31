package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {
    private HashMap<Long, User> users = new HashMap<>();
    @GetMapping
    public Collection<User> getAllFilms(){
        return users.values();
    }

    @PostMapping
    public User createFilm(@RequestBody User user){
        return user;
    }

    @PutMapping
    public User updateFilm(@RequestBody User user){
        return user;
    }

    public boolean validation(User user){
        boolean result = true;
        if(user.getEmail().isEmpty()){
            throw new ValidationException("Имейл не может быть пустым");
        }
        if(!user.getEmail().contains("@")){
            throw new ValidationException("Неверный формат почтового адреса");
        }
        if(user.getLogin().isEmpty()){
            throw new ValidationException("Логин не может быть пустым");
        }
        if(user.getLogin().contains(" ")){
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if(user.getBirthday().isAfter(LocalDate.now())){
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        return result;
    }

}
