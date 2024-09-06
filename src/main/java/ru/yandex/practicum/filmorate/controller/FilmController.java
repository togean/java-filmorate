package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;

@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (validation(film)) {
            // формируем дополнительные данные
            film.setId(getNextId());
            // сохраняем новый фильм
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм \"{}\"", film.getName());
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильма с таким ID нет");
        }
        if (validation(film)) {
            // Обновляем новый фильм
            films.put(film.getId(), film);
        }
        return film;
    }

    public boolean validation(Film film) {
        boolean result = true;
        if (film.getName().isEmpty()) {
            log.info("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.info("Описание не может быть длиннее 200 символов");
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }
        if (film.getDuration() <= 0) {
            log.info("Длительность фильма должна быть больше 0");
            throw new ValidationException("Длительность фильма должна быть больше 0");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Производство фильма не может быть ранее 28 декабря 1895 года");
            throw new ValidationException("Производство фильма не может быть ранее 28 декабря 1895 года");
        }

        return result;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
