package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmService {
    List<Film> getAllFilms();

    Film getFilmById(Integer filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);
}
