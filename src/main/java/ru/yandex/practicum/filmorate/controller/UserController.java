package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
        return userService.getUserById(Integer.parseInt(userId));
    }

    @GetMapping("/users/{userId}/friends")
    public Set<User> getUserFriends(@PathVariable("userId") String userId) {
        return userService.getUserFriends(Integer.parseInt(userId));
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public List<User> getUsersCommonFriends(@PathVariable("userId") String userId, @PathVariable("otherId") String otherId) {
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
    public void addFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {
        userService.addFriend(Integer.valueOf(userId), Integer.valueOf(friendId));
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable("userId") String userId, @PathVariable("friendId") String friendId) {
        userService.deleteFriend(Integer.valueOf(userId), Integer.valueOf(friendId));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("Ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("Ошибка", e.getMessage());
    }

    @ExceptionHandler({Exception.class, NoSuchElementException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerErrorException(final InternalServerErrorException e) {
        return Map.of("Ошибка", e.getMessage());
    }
}
