package ru.yandex.practicum.event;

 import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
