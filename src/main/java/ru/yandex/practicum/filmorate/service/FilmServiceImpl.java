package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    @Autowired
    private final FilmStorage filmStorage;
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final GenreServiceImpl genreService;


    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        if (validation(film)) {
            if (validateGenre(film)) {
                return filmStorage.createFilm(film);
            }
        }
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id фильма должен быть указан");
        }
        Optional<Film> filmToBeUpdated = Optional.ofNullable(filmStorage.getFilmById(film.getId()));
        if (filmToBeUpdated.isEmpty()) {
            throw new NotFoundException("Фильма с таким ID нет");
        }
        if (validation(film)) {
            if (validateGenre(film)) {
                // Обновляем существующий фильм
                return filmStorage.updateFilm(film);
            }
        }
        return null;
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Запрошено {} популярных фильмов", count);
        //Ограничиваем размер запроса размером имеющегося у нас списка фильмов
        // if (filmStorage.getAllFilms().size() < count) {
         //   count = filmStorage.getAllFilms().size();
        //}
        log.info("Будет запрошено {} популярных фильмов", count);
        List<Film> listOfPopularFilms = new ArrayList<>(count);
        //Если фильмы в списке есть
        if (count > 0) {
            List<Film> listOfAllFilms = filmStorage.getAllFilms();
            listOfAllFilms.sort((o1, o2) -> {
                if (o2.getUsersWhoSetLikes() != null) {
                    if (o1.getUsersWhoSetLikes() != null) {
                        return o2.getUsersWhoSetLikes().size() - o1.getUsersWhoSetLikes().size();
                    }
                }
                return 0;
            });
            int filmsCount = 0;
            for (Film film : listOfAllFilms) {
                listOfPopularFilms.add(film);
                filmsCount++;
                if (filmsCount == count) {
                    break;
                }
            }
        }
        return listOfPopularFilms;
    }

    public void setLike(Integer filmId, Integer userId) {
        Optional<Film> filmToBeLiked = Optional.ofNullable(filmStorage.getFilmById(filmId));
        Optional<User> userWhoLikesFilm = Optional.ofNullable(userStorage.getUserById(userId));
        if (filmToBeLiked.isEmpty() || userWhoLikesFilm.isEmpty()) {
            throw new NotFoundException("Нет такого фильма или пользователя");
        }
        Set<Integer> newListOfUsersWhoLikesFilm = new HashSet<>();
        Optional<Set<Integer>> oldListOfUsersWhoLikesFilm = Optional.ofNullable(filmToBeLiked.get().getUsersWhoSetLikes());
        if (oldListOfUsersWhoLikesFilm.isEmpty()) {
            newListOfUsersWhoLikesFilm.add(userId);
        } else {
            newListOfUsersWhoLikesFilm.addAll(oldListOfUsersWhoLikesFilm.get());
            newListOfUsersWhoLikesFilm.add(userId);
        }
        filmToBeLiked.get().setUsersWhoSetLikes(newListOfUsersWhoLikesFilm);
        filmStorage.updateFilm(filmToBeLiked.get());
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Optional<Film> filmToBeNotLiked = Optional.ofNullable(filmStorage.getFilmById(filmId));
        Optional<User> userWhoDoesNotLikeFilm = Optional.ofNullable(userStorage.getUserById(userId));
        if (filmToBeNotLiked.isEmpty() || userWhoDoesNotLikeFilm.isEmpty()) {
            throw new NotFoundException("Нет такого фильма или пользователя");
        }
        Set<Integer> newListOfUsersWhoLikesFilm = new HashSet<>();
        Optional<Set<Integer>> oldListOfUsersWhoLikesFilm = Optional.ofNullable(filmToBeNotLiked.get().getUsersWhoSetLikes());
        if (oldListOfUsersWhoLikesFilm.isPresent()) {
            newListOfUsersWhoLikesFilm.addAll(oldListOfUsersWhoLikesFilm.get());
            if (newListOfUsersWhoLikesFilm.contains(userId)) {
                newListOfUsersWhoLikesFilm.remove(userId);
                filmToBeNotLiked.get().setUsersWhoSetLikes(newListOfUsersWhoLikesFilm);
                filmStorage.updateFilm(filmToBeNotLiked.get());
            }
        }
    }

    private boolean validateGenre(Film film) {
        //Запрашиваем все жанры в базе для сравнения
        List<Genre> allGenres = genreService.getAllGenres();
        if (film.getGenres() == null) {
            //throw new NotFoundException("Жанр не может быть пустым");
            return true;
        }
        //Для проверки смотрим каждый жанр, указанный в новом фильме
        for (Genre genre : film.getGenres()) {
            boolean ganreIsInDatabase = false;
            //И сверяем жанр фильма с теми, что есть в базе
            for (Genre genreToCompare : allGenres) {
                //Если нашли очередной жанр, выставляем флаг
                if (genreToCompare.getId().equals(genre.getId())) {
                    ganreIsInDatabase = true;
                    break;
                }
            }
            //Если по проверяемому жанру фильма мы не нашли такой же жанр в базе (это новый жанр), то ошибка NotFound. Пока новые жанры НЕ добавляются
            if (!ganreIsInDatabase) {
                throw new ValidationException("Жанра '" + genre + "' нет в базе данных");
            }
        }
        return true;
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
}
