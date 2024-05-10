package ru.yandex.practicum.request;

import ru.yandex.practicum.event.Event;
import ru.yandex.practicum.user.User;

import java.time.LocalDateTime;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus().name()
        );
    }

    public static Request toRequest(User user, Event event) {
        return Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(RequestStatus.PENDING)
                .build();
    }
}
