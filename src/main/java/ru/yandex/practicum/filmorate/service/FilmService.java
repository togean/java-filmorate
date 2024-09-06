package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@Service
public class FilmService {
    private static InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();

    public Collection<Film> getAllUsers() {
        return filmStorage.getAllFilms();
    }

    public static Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public static Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public static Film updateFilm(Film user) {
        return filmStorage.updateFilm(user);
    }
}
