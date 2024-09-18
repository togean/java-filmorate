package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.FilmorateApplication.log;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserStorage userStorage;

    @Override
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(Integer userId, Integer friendId) {
        Optional<User> friend1 = Optional.ofNullable(userStorage.getUserById(userId));
        Optional<User> friend2 = Optional.ofNullable(userStorage.getUserById(friendId));
        if (friend1.isEmpty() || friend2.isEmpty()) {
            log.info("Один или оба пользователя для добавления в друзья не найдены");
            throw new NotFoundException("Один или оба пользователя для добавления в друзья не найдены");
        }
        Optional<Set<Integer>> listOfFriendsForUser1 = Optional.ofNullable(userStorage.getUserById(userId).getFriends());
        Optional<Set<Integer>> listOfFriendsForUser2 = Optional.ofNullable(userStorage.getUserById(friendId).getFriends());
        Set<Integer> newListOfFriendsForUser1 = new HashSet<>();
        Set<Integer> newListOfFriendsForUser2 = new HashSet<>();
        if (listOfFriendsForUser1.isPresent()) {
            newListOfFriendsForUser1.addAll(listOfFriendsForUser1.get());
            newListOfFriendsForUser1.add(friend2.get().getId());
        } else {
            newListOfFriendsForUser1.add(friend2.get().getId());
        }
        if (listOfFriendsForUser2.isPresent()) {
            newListOfFriendsForUser2.addAll(listOfFriendsForUser2.get());
            newListOfFriendsForUser2.add(friend1.get().getId());
        } else {
            newListOfFriendsForUser2.add(friend1.get().getId());
        }
        friend1.get().setFriends(newListOfFriendsForUser1);
        friend2.get().setFriends(newListOfFriendsForUser2);
        userStorage.updateUser(friend1.get());
        userStorage.updateUser(friend2.get());
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        Optional<User> friend1 = Optional.ofNullable(userStorage.getUserById(userId));
        Optional<User> friend2 = Optional.ofNullable(userStorage.getUserById(friendId));
        if (friend1.isEmpty() || friend2.isEmpty()) {
            log.info("Один или оба пользователя для удаления из друзей не найдены");
            throw new NotFoundException("Один или оба пользователя для удаления из друзей не найдены");
        }
        Optional<Set<Integer>> listOfFriendsForUser1 = Optional.ofNullable(userStorage.getUserById(userId).getFriends());
        Optional<Set<Integer>> listOfFriendsForUser2 = Optional.ofNullable(userStorage.getUserById(friendId).getFriends());
        if (listOfFriendsForUser1.isEmpty() || listOfFriendsForUser2.isEmpty()) {
            log.info("Один или оба пользователя не имеют друзей");
        } else {
            Set<Integer> newListOfFriendsForUser1 = new HashSet<>();
            Set<Integer> newListOfFriendsForUser2 = new HashSet<>();

            newListOfFriendsForUser1.addAll(listOfFriendsForUser1.get());
            newListOfFriendsForUser2.addAll(listOfFriendsForUser2.get());
            if ((!newListOfFriendsForUser2.contains(userId)) || (!newListOfFriendsForUser1.contains(friendId))) {
                log.info("Выбранные пользователя не являются друзьями");
            }

            newListOfFriendsForUser1.remove(friendId);
            newListOfFriendsForUser2.remove(userId);
            friend1.get().setFriends(newListOfFriendsForUser1);
            friend2.get().setFriends(newListOfFriendsForUser2);
            userStorage.updateUser(friend1.get());
            userStorage.updateUser(friend2.get());
        }
    }

    public Set<User> getUserFriends(Integer userId) {
        Set<User> listOfFriends = new HashSet<>();
        Optional<User> userToShowFriends = Optional.ofNullable(userStorage.getUserById(userId));
        if (userToShowFriends.isEmpty()) {
            log.info("Пользователь с ID {} для вывода его друзей не найден", userId);
            throw new NotFoundException("Пользователь для вывода его друзей не найден");
        }
        Optional<Set<Integer>> listOfUserFriends = Optional.ofNullable(userStorage.getUserById(userId).getFriends());
        if (listOfUserFriends.isEmpty()) {
            log.info("У пользователя нет друзей");
            return listOfFriends;
        }
        for (Integer i : listOfUserFriends.get()) {
            listOfFriends.add(userStorage.getUserById(i));
        }
        return listOfFriends;
    }

    public List<User> getUsersCommonFriends(Integer userId, Integer otherId) {
        List<User> listOfCommonFriends = new ArrayList<>();
        Optional<User> firstUser = Optional.ofNullable(userStorage.getUserById(userId));
        Optional<User> otherUser = Optional.ofNullable(userStorage.getUserById(otherId));
        if (firstUser.isEmpty() || otherUser.isEmpty()) {
            throw new NotFoundException("Одного или двух пользователей нет в системе");
        }
        Optional<Set<Integer>> listOfFriendsOfFirstUser = Optional.ofNullable(firstUser.get().getFriends());
        Optional<Set<Integer>> listOfFriendsOfOtherUser = Optional.ofNullable(otherUser.get().getFriends());
        if (listOfFriendsOfFirstUser.isEmpty() || listOfFriendsOfOtherUser.isEmpty()) {
            throw new NotFoundException("Общие друзья не найдены");
        }
        List<Integer> commonList = new ArrayList<>();
        commonList.addAll(listOfFriendsOfFirstUser.get());
        commonList.retainAll(listOfFriendsOfOtherUser.get());

        for (Integer i : commonList) {
            listOfCommonFriends.add(userStorage.getUserById(i));
        }
        return listOfCommonFriends;
    }
}
