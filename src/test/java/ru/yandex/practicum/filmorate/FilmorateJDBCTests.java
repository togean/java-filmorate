package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaInstance;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmsRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRateRepository;
import ru.yandex.practicum.filmorate.repository.UsersRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate.repository")
public class FilmorateJDBCTests {
    private final UsersRepository userStorage;
    private final FilmsRepository filmStorage;
    private final GenreRepository genreStorage;
    private final MpaRateRepository mpaStorage;

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindFilmById() {

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindGenreById() {

        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getGenreById(1));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindMpaById() {

        Optional<MpaInstance> mpaOptional = Optional.ofNullable(mpaStorage.getMpaRateById(1));
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1)
                );
    }

}
