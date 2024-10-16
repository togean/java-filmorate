package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;


public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        List<Film> listOfFilms = new ArrayList<>();
        for (Map.Entry<Integer, Film> entry : films.entrySet()) {
            listOfFilms.add(entry.getValue());
        }
        return listOfFilms;
    }

    public Film getFilmById(Integer filmId) {
        Optional<Film> requestedFilm = Optional.ofNullable(films.get(filmId));
        if (requestedFilm.isEmpty()) {
            throw new NotFoundException("Такого фильманет");
        }
        return films.get(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        if (validation(film)) {
            // формируем дополнительные данные
            film.setId(getNextId());
            // сохраняем новый фильм
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм \"{}\"", film.getName());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильма с таким ID нет");
        }
        if (validation(film)) {
            // Обновляем новый фильм
            films.put(film.getId(), film);
        }
        return film;
    }

    private boolean validation(Film film) {
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
