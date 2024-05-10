package ru.yandex.practicum.event.service;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.*;
import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEventByFilter(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                         Integer from, Integer size);

    EventFullDto getEventById(Integer id);

    List<EventFullDto> getEventsForAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    @Transactional
    EventFullDto getPublicEventById(Integer id);

    EventFullDto updateEventByIdAdmin(Integer eventId, UpdateEventAdminRequest dto);

    List<EventShortDto> getAllEventsByUserId(Integer userId, int from, int size);

    EventFullDto addNewEvent(Integer userId, NewEventDto dto);

    EventFullDto getEventByUserId(Integer userId, Integer eventId);

    @Transactional
    EventFullDto updateEventByUserId(Integer userId, Integer eventId, UpdateEventAdminRequest dto);

    List<ParticipationRequestDto> getAllRequestsByUserAndEvent(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(Integer userId,
                                                            Integer eventId,
                                                            EventRequestStatusUpdateRequest request);
}
