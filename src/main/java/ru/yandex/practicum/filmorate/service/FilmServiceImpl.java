package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;

@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    @Autowired
    private final FilmStorage filmStorage;
    @Autowired
    private final UserStorage userStorage;

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
        return filmStorage.createFilm(film);
    }

    @Override
    public Film updateFilm(Film user) {
        return filmStorage.updateFilm(user);
    }

    public List<Film> getPopularFilms(Integer count) {
        log.info("Запрошено {} популярных фильмов", count);
        //Ограничиваем размер запроса размером имеющегося у нас списка фильмов
        count = filmStorage.getAllFilms().size();
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
}
