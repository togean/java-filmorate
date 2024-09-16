package ru.yandex.practicum.filmorate;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
public class UserControllerTest {
    private final UserController userController = new UserController(new UserServiceImpl(new InMemoryUserStorage()));

    @Test
    public void userShouldHaveLogin() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@ask.him");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2000, 12, 5));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void userloginShouldNotHaveSpace() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@ask.him");
        user.setLogin("lo gin");
        user.setBirthday(LocalDate.of(2000, 12, 5));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void useremailShouldPresent() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 12, 5));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void useremailShouldBeRight() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("useremail");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 12, 5));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    public void userBirthdayShouldNotBeInFuture() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("user@email");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2080, 12, 5));
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }
}
