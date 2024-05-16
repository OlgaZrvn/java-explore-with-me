package ru.yandex.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.Event;
import ru.yandex.practicum.event.EventState;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.ParticipationRequestDto;
import ru.yandex.practicum.request.Request;
import ru.yandex.practicum.request.RequestMapper;
import ru.yandex.practicum.request.RequestStatus;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addNewRequest(Integer userId, Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        validateRequest(requester, event);
        Request request = RequestMapper.toRequest(requester, event);

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        request = requestRepository.save(request);
        if (request.getStatus() == RequestStatus.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateRequest(Integer userId, Integer requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private void validateRequest(User requester, Event event) {
        if (requester.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор события не может быть его участником");
        }
        List<Request> requests = requestRepository.findAllByEventIdAndRequesterId(event.getId(), requester.getId());
        if (!requests.isEmpty()) {
            throw new ConflictException("Заявка уже существует");
        }
        if (!(event.getState() == EventState.PUBLISHED)) {
            throw new ConflictException("Заявку можно создать только для опубликованного события");
        }
        if (event.getParticipantLimit() > 0) {
            Long participants = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
            Integer limit = event.getParticipantLimit();
            if (participants >= limit) {
                throw new ConflictException("Достигнуто максимальное количество заявок");
            }
        }
    }
}
