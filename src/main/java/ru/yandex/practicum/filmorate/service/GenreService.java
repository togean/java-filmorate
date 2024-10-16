package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> getAllGenres();

    Genre getGenreById(Integer generId);

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre genre);
}
