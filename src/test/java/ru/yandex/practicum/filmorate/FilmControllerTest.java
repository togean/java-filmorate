package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
@Component
public class FilmControllerTest {
    private final FilmController filmController = new FilmController(new FilmServiceImpl(new InMemoryFilmStorage(), new InMemoryUserStorage()));

    @Test
    public void filmShouldHaveName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(2000, 12, 5));
        film.setDuration(90);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void filmDurationShouldBeMoreZero() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(2000, 12, 5));
        film.setDuration(-90);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void filmDescriptionShouldNotBeMore200Characters() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - Very-very-very-long-description - ");
        film.setReleaseDate(LocalDate.of(2000, 12, 5));
        film.setDuration(90);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void filmCanNotBeProducedBefore1895_12_28() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 01, 05));
        film.setDuration(90);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }
}
