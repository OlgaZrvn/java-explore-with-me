package ru.yandex.practicum.user.service;

import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.User;

import java.util.List;

public interface UserService {
    List<User> getUsersByIds(List<Integer> ids, int from, int size);

    User addNewUser(NewUserRequest user);

    void deleteUserById(Integer userId);
}
