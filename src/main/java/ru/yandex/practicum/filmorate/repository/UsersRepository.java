package ru.yandex.practicum.filmorate.repository;

import jakarta.annotation.Priority;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendShipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
public class UsersRepository implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("select * from users order by user_id", mapUser());
    }

    @Override
    public User createUser(User user) {
        String email = user.getEmail();
        String name = user.getName();
        LocalDate birthday = user.getBirthday();
        String login = user.getLogin();
        String sqlStatement = "INSERT INTO users (name, login, email, birthday) VALUES ('" + name + "','" + login + "','" + email + "','" + birthday + "')";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        int result = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            return preparedStatement;
        }, keyHolder);
        if (result == 0) {
            log.info("Добавления не произошло");
            return null;
        } else {
            Map<String, Object> keys = keyHolder.getKeys();
            Long id = (Long) keys.get("user_id");
            return getUserById(id.intValue());
        }
    }

    @Override
    public User updateUser(User user) {
        int userId = user.getId();
        String name = user.getName();
        String login = user.getLogin();
        LocalDate birthday = user.getBirthday();
        String email = user.getEmail();
        Set<Integer> friends = user.getFriends();
        Map<Integer, FriendShipStatus> friendShipStatusMap = user.getFriendsStatuses();

        String sqlStatement = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE user_id = ?";
        int result = jdbcTemplate.update(sqlStatement, name, login, email, birthday, userId);
        if (result == 0) {
            log.info("Обновления не произошло");
            return null;
        }
        if (!(friends == null)) {
            log.info("Кол-во друзей {}", friends.size());
            sqlStatement = "DELETE FROM users_friends  WHERE user_id =" + userId;
            jdbcTemplate.execute(sqlStatement);
            for (Integer i : friends) {
                String status = friendShipStatusMap.get(i).toString();
                sqlStatement = "INSERT INTO users_friends (user_id, friend_id, status_id) VALUES (?,?,?)";
                result = jdbcTemplate.update(sqlStatement, userId, i, getFriendStatusByName(status));
                if (result == 0) {
                    log.info("Обновления не произошло");
                    return null;
                }
            }
        }
        return getUserById(userId);
    }

    @Override
    public User getUserById(Integer userId) {
        User requestedUser = new User();
        try {
            requestedUser = jdbcTemplate.queryForObject("select * from users where user_id = ?", mapUser(), userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return requestedUser;
    }


    private RowMapper<User> mapUser() {
        return (rs, rowNum) -> {
            Set<Integer> listOfFriends = new HashSet<>();
            Map<Integer, FriendShipStatus> friendsStatuses = new HashMap<>();
            User selectedUser = new User();
            selectedUser.setId(rs.getInt("user_id"));
            selectedUser.setName(rs.getString("name"));
            selectedUser.setEmail(rs.getString("email"));
            selectedUser.setLogin(rs.getString("login"));
            selectedUser.setBirthday(rs.getDate("birthday").toLocalDate());
            getFriends(selectedUser, listOfFriends, friendsStatuses);
            selectedUser.setFriends(listOfFriends);
            selectedUser.setFriendsStatuses(friendsStatuses);
            return selectedUser;
        };
    }

    private void getFriends(User selectedUser, Set<Integer> listOfFriends, Map<Integer, FriendShipStatus> friendsStatuses) {
        jdbcTemplate.query("select * from users_friends WHERE user_id = ?", new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                listOfFriends.add(rs.getInt("friend_id"));
                friendsStatuses.put(rs.getInt("friend_id"), FriendShipStatus.valueOf(getFriendStatus(rs.getInt("status_id"))));
            }
        }, selectedUser.getId());
    }

    private String getFriendStatus(int Status) {
        switch (Status) {
            case 1:
                return "Подтверждён";
            case 2:
                return "Неподтверждён";
        }
        return null;
    }

    private int getFriendStatusByName(String Status) {
        switch (Status) {
            case "Подтверждён":
                return 1;
            case "Неподтверждён":
                return 2;
        }
        return 0;
    }
}
