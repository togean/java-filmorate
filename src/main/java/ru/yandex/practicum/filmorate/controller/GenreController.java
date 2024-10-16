package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreServiceImpl;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping
@AllArgsConstructor
public class GenreController {
    private GenreServiceImpl genreService;

    @GetMapping("/genres")
    public Collection<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/genres/{genre_id}")
    public Genre getGenreById(@PathVariable("genre_id") String genreId) {
        return genreService.getGenreById(Integer.valueOf(genreId));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("Ошибка", e.getMessage());
    }
}
