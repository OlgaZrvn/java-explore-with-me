package ru.yandex.practicum;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HitMapper {

    Hit toHit(EndpointHit endpointHit);

    EndpointHit toHitDto (Hit hit);
}
