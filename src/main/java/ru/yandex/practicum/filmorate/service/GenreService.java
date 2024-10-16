package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public interface GenreService {
    List<Genre> getAllGenres();

    Genre getGenreById(Integer generId);

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre genre);
}
