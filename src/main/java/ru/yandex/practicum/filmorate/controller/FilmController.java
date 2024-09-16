package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.InternalServerErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping
@AllArgsConstructor
public class FilmController {
    @Autowired
    private FilmServiceImpl filmService;

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable("filmId") String filmId) {
        return filmService.getFilmById(Integer.valueOf(filmId));
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public void setLikeToFilm(@PathVariable("filmId") String filmId, @PathVariable("userId") String userId) {
        filmService.setLike(Integer.valueOf(filmId), Integer.valueOf(userId));
        //return filmService.setLike(Integer.valueOf(filmId), Integer.valueOf(userId));
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public void deleteFilmLike(@PathVariable("filmId") String filmId, @PathVariable("userId") String userId) {
        filmService.deleteLike(Integer.valueOf(filmId), Integer.valueOf(userId));
        //return filmService.deleteLike(Integer.valueOf(filmId), Integer.valueOf(userId));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("Ошибка", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("Ошибка", e.getMessage());
    }

    @ExceptionHandler({Exception.class, NoSuchElementException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerErrorException(final InternalServerErrorException e) {
        return Map.of("Ошибка", e.getMessage());
    }

}
