package ru.yandex.practicum.filmorate.repository;

import jakarta.annotation.Priority;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerErrorException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaInstance;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;

@Component
@AllArgsConstructor
@Priority(1)
@Repository
public class FilmsRepository implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilms = new ArrayList<>();
        List<Film> allFilmsWithGenres = new ArrayList<>();

        String sql = "select * from films as f join films_genres fg on fg.film_id = f.film_id join genres as g on fg.genre_id = g.genre_id join rates as r on f.rateid = r.rate_id";

        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Film selectedFilm = new Film();
                selectedFilm.setId(rs.getInt("film_id"));
                selectedFilm.setName(rs.getString("name"));
                selectedFilm.setDescription(rs.getString("description"));
                selectedFilm.setReleaseDate(rs.getDate("film_releasedate").toLocalDate());
                selectedFilm.setDuration(rs.getInt("duration"));
                if (!(rs.getString("ratename") == null)) {
                    MpaInstance mpaRate = new MpaInstance();
                    mpaRate.setId(rs.getInt("rate_id"));
                    mpaRate.setName(rs.getString("ratename"));
                    selectedFilm.setMpa(mpaRate);
                }
                Genre genre = new Genre();
                if (!(rs.getString("genrename") == null)) {
                    genre.setName(rs.getString("genrename"));
                    genre.setId(rs.getInt("genre_id"));
                }
                fillInGenres(allFilms, allFilmsWithGenres, selectedFilm, genre);
            }
        });

        sql = "select f.film_id, f.user_id from films_users as f";
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Integer userid = rs.getInt("user_id");
                Integer filmid = rs.getInt("film_id");
                getUsers(allFilmsWithGenres, userid, filmid);

            }
        });
        return allFilmsWithGenres;
    }

    private void fillInGenres(List<Film> filmList, List<Film> filmListWithGenres, Film filmToCheck, Genre genre) {
        SortedSet<Genre> genres = new TreeSet<>();
        if (filmList.contains(filmToCheck)) {
            Film updatedFilm = filmToCheck;
            int filmId = filmList.indexOf(filmToCheck);
            genres.clear();
            genres = filmListWithGenres.get(filmId).getGenres();
            genres.add(genre);
            //У id вычитаем 1 т.к. в БД индекс идёт не от 0, а от 1
            filmId = filmListWithGenres.get(filmId).getId() - 1;
            try {
                updatedFilm = filmToCheck.clone();
            } catch (CloneNotSupportedException ex) {
                throw new InternalServerErrorException("Неполучилось клонировать объект Film");
            }
            genres.stream().sorted(new Comparator<Genre>() {
                @Override
                public int compare(Genre o1, Genre o2) {
                    return o1.getId() - o2.getId();
                }
            });
            updatedFilm.setGenres(genres);
            filmListWithGenres.set(filmId, updatedFilm);
        }
        if (!filmList.contains(filmToCheck)) {
            //Сюда склонируем фильм, что бы изменение в нём не повлияли на логику
            Film filmWithGenres = new Film();
            filmList.add(filmToCheck);
            try {
                filmWithGenres = filmToCheck.clone();
            } catch (CloneNotSupportedException ex) {
                throw new InternalServerErrorException("Неполучилось клонировать объект Film");
            }
            genres.clear();
            genres.add(genre);
            filmWithGenres.setGenres(genres);
            filmListWithGenres.add(filmWithGenres);
        }
    }

    //Выборка пользователей для фильма
    private void getUsers(List<Film> filmListWithGenres, Integer userid, Integer filmId) {
        Set<Integer> usersWhoLikeFilm = new HashSet<>();
        for (Film film : filmListWithGenres) {
            if (film.getId() == filmId) {
                if (!(film.getUsersWhoSetLikes() == null)) {
                    usersWhoLikeFilm.addAll(film.getUsersWhoSetLikes());
                }
                usersWhoLikeFilm.add(userid);
                film.setUsersWhoSetLikes(usersWhoLikeFilm);
            }
        }
    }

    @Override
    public Film createFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String desc = film.getDescription();
        String name = checkName(film.getName());
        LocalDate releasedate = film.getReleaseDate();
        Integer duration = film.getDuration();
        MpaInstance mpa = null;
        if (!(film.getMpa() == null)) {
            mpa = film.getMpa();
        }
        String sqlStatement = "INSERT INTO films (name, description, film_releasedate, duration, rateid)" +
                " VALUES ('" + name + "','" + desc + "','" + releasedate + "'," + duration + "," + mpa.getId() + ")";
        int result = 0;
        try {
            result = jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
                return preparedStatement;
            }, keyHolder);
        } catch (RuntimeException e) {
            throw new ValidationException("Ошибка в запросе на создание фильма");
        }
        if (result == 0) {
            log.info("Добавления не произошло");
            return null;
        } else {
            //Если успешно добавили фильм, то надо добавить в базу и его жанры
            Map<String, Object> keys = keyHolder.getKeys();
            SortedSet<Genre> filmGenres = film.getGenres();
            if (!(film.getGenres() == null)) {
                String genreSqlStatement = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?)";
                for (Genre genre : filmGenres) {
                    int genreId = genre.getId();//Получаем ID-шник жанра
                    //Добавляем жанры в базу
                    int genreInsertionResult = jdbcTemplate.update(genreSqlStatement, keys.get("film_id"), genreId);
                    if (genreInsertionResult == 0) {
                        log.info("Обновления жанра не произошло");
                    }
                }
            }
            film.setGenres(filmGenres);
            Long id = (Long) keys.get("film_id");
            return getFilmById(id.intValue());
        }
    }

    private String checkName(String name) {
        if (name.contains("'")) {
            return name.replace("'", "''");
        }
        return name;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        String desc = film.getDescription();
        String name = film.getName();
        LocalDate releasedate = film.getReleaseDate();
        Integer duration = film.getDuration();
        Set<Genre> filmGenres = film.getGenres();
        MpaInstance mpa = null;
        if (!(film.getMpa() == null)) {
            mpa = film.getMpa();
        }
        Set<Integer> listOfUsersWhoSetLikes = film.getUsersWhoSetLikes();
        String sqlStatement = "UPDATE films SET description = ?, name = ?, film_releasedate = ?, duration = ?, rateid = ? WHERE film_id = ?";
        int result = jdbcTemplate.update(sqlStatement, desc, name, releasedate, duration, mpa.getId(), filmId);
        if (result == 0) {
            log.info("Обновления не произошло");
            return null;
        } else {
            //Удаляяем старые данные о лайках фильма
            String sqlStatementToDeleteOldRecordsAboutLikes = "DELETE FROM films_users WHERE film_id = ?";
            int deleteResult = jdbcTemplate.update(sqlStatementToDeleteOldRecordsAboutLikes, filmId);

            //Добавляем новые данные о лайках фильма
            if (!(listOfUsersWhoSetLikes == null)) {
                String sqlStatementForUsers = "INSERT INTO films_users (film_id, user_id) VALUES (?,?)";
                for (Integer i : listOfUsersWhoSetLikes) {
                    int updateResult = jdbcTemplate.update(sqlStatementForUsers, filmId, i);
                }
            }
            //Удаляем данные о связях с жанрами фильма
            String sqlStatementToDeleteOldRelationsWithGanres = "DELETE FROM films_genres WHERE film_id = ?";
            deleteResult = jdbcTemplate.update(sqlStatementToDeleteOldRelationsWithGanres, filmId);

            //Добавляем данные о связях с жанрами фильма
            String sqlStatementToAddGanres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?,?)";
            if (!(filmGenres == null)) {
                for (Genre genre : filmGenres) {
                    //int genreId = ConvertGenreNameToGenreId(genre);//Получаем ID-шник жанра
                    int genreId = genre.getId();//Получаем ID-шник жанра
                    int updateResult = jdbcTemplate.update(sqlStatementToAddGanres, filmId, genreId);
                }
            }

            return getFilmById(filmId);
        }
    }

    @Override
    public Film getFilmById(Integer filmId) {
        String sql = "select f.film_id, f.name, f.description, f.film_releasedate, f.duration, r.ratename, r.rate_id, g.genre_id, g.genrename, fu.user_id from films as f left join films_genres as fg on f.film_id=fg.film_id left join genres as g on fg.genre_id = g.genre_id left join rates as r on f.rateid = r.rate_id left join films_users as fu on fu.film_id = f.film_id where f.film_id = ?";
        return jdbcTemplate.queryForObject(sql, mapFilm(), filmId);
    }

    private RowMapper<Film> mapFilm() {
        return (rs, rowNum) -> {
            log.info("Обрабатываю {} строчку", rowNum);
            Film selectedFilm = new Film();
            selectedFilm.setId(rs.getInt("film_id"));
            selectedFilm.setName(rs.getString("name"));
            selectedFilm.setDescription(rs.getString("description"));
            selectedFilm.setReleaseDate(rs.getDate("film_releasedate").toLocalDate());
            selectedFilm.setDuration(rs.getInt("duration"));
            if (!(rs.getString("ratename") == null)) {
                MpaInstance mpaRate = new MpaInstance();
                mpaRate.setId(rs.getInt("rate_id"));
                mpaRate.setName(rs.getString("ratename"));
                selectedFilm.setMpa(mpaRate);
            }
            SortedSet<Genre> filmGenres = new TreeSet<>();
            Set<Integer> usersWhoLikeFilm = new HashSet<>();
            do {
                if (rs.getInt("genre_id") > 0) {
                    Integer genre = rs.getInt("genre_id");
                    Genre genreToAdd = new Genre();
                    genreToAdd.setId(genre);
                    genreToAdd.setName(rs.getString("genrename"));
                    filmGenres.add(genreToAdd);
                }
                int user = rs.getInt("user_id");
                //У фильма может не быть пользователей, а 0-ли нам добавлять в таком слечае не надо
                if (user > 0) {
                    usersWhoLikeFilm.add(user);
                }
            } while (rs.next());
            selectedFilm.setGenres(filmGenres);
            selectedFilm.setUsersWhoSetLikes(usersWhoLikeFilm);
            return selectedFilm;
        };
    }
}
