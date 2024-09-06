package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;

@RestController
@RequestMapping("/films")
public class FilmController {
    private InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {

        return filmStorage.updateFilm(film);
    }
}
