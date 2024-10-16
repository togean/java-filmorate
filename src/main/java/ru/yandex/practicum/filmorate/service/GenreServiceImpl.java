package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreServiceImpl implements GenreService {

    @Autowired
    private final GenreStorage genreStorage;

    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        return genreStorage.getGenreById(genreId);
    }

    @Override
    public Genre createGenre(Genre genre) {
        return genreStorage.createGenre(genre);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        return genreStorage.updateGenre(genre);
    }
}
