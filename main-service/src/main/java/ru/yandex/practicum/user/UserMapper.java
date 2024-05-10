package ru.yandex.practicum.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserShortDto toUserShortDto(User user);

    User toUser(NewUserRequest newUserRequest);
}
