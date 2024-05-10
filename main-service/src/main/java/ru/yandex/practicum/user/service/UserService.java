package ru.yandex.practicum.user.service;

import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.UserShortDto;

import java.util.List;

public interface UserService {
    List<UserShortDto> getUsersByIds(List<Integer> ids, int from, int size);

    UserShortDto addNewUser(NewUserRequest user);

    void deleteUserById(Integer userId);
}
