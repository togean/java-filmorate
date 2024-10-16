package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Repository
@AllArgsConstructor
public class GenreRepository implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("select * from genres order by genre_id", mapGenre());
    }

    @Override
    public Genre createGenre(Genre genre) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Метод пока не реализован.");
    }

    @Override
    public Genre updateGenre(Genre genre) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Метод пока не реализован.");
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        Genre requestedGenre;
        try {
            requestedGenre = jdbcTemplate.queryForObject("select * from genres where genre_id = ?", mapGenre(), genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Нет такого жанра");
        }
        return requestedGenre;
    }

    private RowMapper<Genre> mapGenre() {
        return (rs, rowNum) -> {
            Genre selectedGenre = new Genre();
            selectedGenre.setId(rs.getInt("genre_id"));
            selectedGenre.setName(rs.getString("genrename"));
            return selectedGenre;
        };
    }
}
