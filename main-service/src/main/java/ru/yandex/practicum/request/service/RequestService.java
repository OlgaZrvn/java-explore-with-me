package ru.yandex.practicum.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsByUserId(Integer userId);

    @Transactional
    ParticipationRequestDto addNewRequest(Integer userId, Integer eventId);

    ParticipationRequestDto updateRequest(Integer userId, Integer requestId);
}
