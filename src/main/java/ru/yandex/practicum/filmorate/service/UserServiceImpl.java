package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendShipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
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
        if (validation(user)) {
            return userStorage.createUser(user);
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id пользователя должен быть указан");
        }
        Optional<User> userToBeUpdated = Optional.ofNullable(userStorage.getUserById(user.getId()));
        if (userToBeUpdated.isEmpty()) {
            throw new NotFoundException("Пользователя с таким ID нет");
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с таким ID нет");
        }
        if (validation(user)) {
            // обновлем пользователя
            return userStorage.updateUser(user);
        }
        return null;
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        Optional<User> friend1 = Optional.ofNullable(userStorage.getUserById(userId));
        Optional<User> friend2 = Optional.ofNullable(userStorage.getUserById(friendId));
        if (friend1.isEmpty() || friend2.isEmpty()) {
            log.info("Один или оба пользователя для добавления в друзья не найдены");
            throw new NotFoundException("Один или оба пользователя для добавления в друзья не найдены");
        }
        Optional<Set<Integer>> listOfFriendsForUser1 = Optional.ofNullable(userStorage.getUserById(userId).getFriends());
        Optional<Set<Integer>> listOfFriendsForUser2 = Optional.ofNullable(userStorage.getUserById(friendId).getFriends());
        Set<Integer> newListOfFriendsForUser1 = new HashSet<>();
        if (listOfFriendsForUser1.isPresent()) {
            newListOfFriendsForUser1.addAll(listOfFriendsForUser1.get());
            newListOfFriendsForUser1.add(friend2.get().getId());
        } else {
            newListOfFriendsForUser1.add(friend2.get().getId());
        }
        Optional<Map<Integer, FriendShipStatus>> friendsMapForUser1 = Optional.ofNullable(userStorage.getUserById(userId).getFriendsStatuses());
        Optional<Map<Integer, FriendShipStatus>> friendsMapForUser2 = Optional.ofNullable(userStorage.getUserById(friendId).getFriendsStatuses());
        Map<Integer, FriendShipStatus> newfriendsMapForUser1 = new HashMap<>();
        Map<Integer, FriendShipStatus> newfriendsMapForUser2 = new HashMap<>();
        if (friendsMapForUser1.isPresent()) {
            newfriendsMapForUser1.putAll(friendsMapForUser1.get());
            newfriendsMapForUser1.put(friend2.get().getId(), FriendShipStatus.Неподтверждён);
        } else {
            newfriendsMapForUser1.put(friend2.get().getId(), FriendShipStatus.Неподтверждён);
        }

        //Проверяем, есть ли первый пользователь с друзьях у второго, если есть, то статус будет Подтверждён
        if (listOfFriendsForUser2.get().contains(friend1.get().getId())) {
            newfriendsMapForUser1.put(friend2.get().getId(), FriendShipStatus.Подтверждён);
            if (friendsMapForUser2.isPresent()) {
                newfriendsMapForUser2.putAll(friendsMapForUser2.get());
                newfriendsMapForUser2.put(friend1.get().getId(), FriendShipStatus.Подтверждён);
            } else {
                newfriendsMapForUser2.put(friend1.get().getId(), FriendShipStatus.Подтверждён);
            }
        }
        friend1.get().setFriends(newListOfFriendsForUser1);
        friend1.get().setFriendsStatuses(newfriendsMapForUser1);
        friend2.get().setFriendsStatuses(newfriendsMapForUser2);
        userStorage.updateUser(friend1.get());
        userStorage.updateUser(friend2.get());
        List<User> twoFriends = new ArrayList<>();
        twoFriends.add(friend1.get());
        twoFriends.add(friend2.get());
        return twoFriends;
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
            Optional<Map<Integer, FriendShipStatus>> friendsMapForUser1 = Optional.ofNullable(userStorage.getUserById(userId).getFriendsStatuses());
            Optional<Map<Integer, FriendShipStatus>> friendsMapForUser2 = Optional.ofNullable(userStorage.getUserById(friendId).getFriendsStatuses());
            if (friendsMapForUser1.isPresent()) {
                friendsMapForUser1.get().remove(friendId);
            }
            if (listOfFriendsForUser2.get().contains(userId)) {
                friendsMapForUser2.get().put(userId, FriendShipStatus.Неподтверждён);
            }
            newListOfFriendsForUser1.remove(friendId);
            friend1.get().setFriends(newListOfFriendsForUser1);
            friend1.get().setFriendsStatuses(friendsMapForUser1.get());

            friend2.get().setFriends(newListOfFriendsForUser2);
            friend2.get().setFriendsStatuses(friendsMapForUser2.get());
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

    private boolean validation(User user) {
        boolean result = true;
        if (user.getEmail() == null || user.getEmail().isBlank() || user.getEmail().isEmpty()) {
            result = false;
            log.info("Валидация email не прошла");
            throw new ValidationException("Имейл не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            result = false;
            log.info("Вторая валидация email не прошла");
            throw new ValidationException("Неверный формат почтового адреса");
        }
        if (user.getLogin().isEmpty()) {
            result = false;
            log.info("Валидация логина не прошла");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            result = false;
            log.info("Валидация логина на пробелы не прошла");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            result = false;
            log.info("Валидация на дату рождения не прошла");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        return result;
    }
}
