package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InternalServerErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import java.util.*;

@RestController
@RequestMapping
@AllArgsConstructor
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable("userId") String userId) {
        if (userId.equals(null)) {
            throw new ValidationException("Не указан правильный ID пользователя");
        }
        return userService.getUserById(Integer.parseInt(userId));
    }

    @GetMapping("/users/{userId}/friends")
    public Set<User> getUserFriends(@PathVariable("userId") String userId) {
        if (userId.equals(null)) {
            throw new ValidationException("Не указан правильный ID пользователя");
        }
        return userService.getUserFriends(Integer.parseInt(userId));
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> getUsersCommonFriends(@PathVariable("userId") String userId, @PathVariable("otherId") String otherId) {
        if (userId.equals(null) || otherId.equals(null)) {
            throw new ValidationException("Не указаны правильные ID пользователей");
        }
        return userService.getUsersCommonFriends(Integer.parseInt(userId), Integer.valueOf(otherId));
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/users/{userId}/friends/{friendId}")
    public List<User> addFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {
        return userService.addFriend(Integer.valueOf(userId), Integer.valueOf(friendId));
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {
        userService.deleteFriend(Integer.valueOf(userId), Integer.valueOf(friendId));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, NumberFormatException.class, IllegalArgumentException.class, NumberFormatException.class, ValidationException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final RuntimeException e) {
        return Map.of("Ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("Ошибка", e.getMessage());
    }

    @ExceptionHandler({Exception.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerErrorException(final InternalServerErrorException e) {
        return Map.of("Ошибка", e.getMessage());
    }
}
