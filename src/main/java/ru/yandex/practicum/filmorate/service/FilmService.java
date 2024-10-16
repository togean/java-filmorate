package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAllFilms();

    Film getFilmById(Integer filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);
}
