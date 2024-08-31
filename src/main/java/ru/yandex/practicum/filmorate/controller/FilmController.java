package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Long, Film> films = new HashMap<>();
    @GetMapping
    public Collection<Film> getAllFilms(){
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film){
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film){
        return film;
    }
    public boolean validation(Film film){
        boolean result = true;
        if(film.getName().isEmpty()){
            throw new ValidationException("Название не может быть пустым");
        }
        if(film.getDescription().length()>200){
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }
        if(film.getDuration()<=0){
            throw new ValidationException("Длительность фильма должно быть больше 0");
        }

        return result;
    }
}
