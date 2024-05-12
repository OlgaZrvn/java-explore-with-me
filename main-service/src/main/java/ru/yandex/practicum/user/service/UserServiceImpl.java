package ru.yandex.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserMapper;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<User> getUsersByIds(List<Integer> ids, int from, int size) {
        if (ids.isEmpty()) {
            return userRepository.findAll(PageRequest.of(from, size))
                    .stream()
                    .collect(Collectors.toList());
        }
        return userRepository.findAllByIdIn(ids, PageRequest.of(from, size))
                .stream()
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public User addNewUser(NewUserRequest user) {
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        return userRepository.save(userMapper.toUser(user));
    }

    @Override
    public void deleteUserById(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.deleteById(userId);
    }
}
