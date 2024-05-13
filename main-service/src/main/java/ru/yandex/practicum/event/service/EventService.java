package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.*;
import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEventByFilter(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                         int from, int size, HttpServletRequest request);

    List<EventFullDto> getEventsForAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto getPublicEventById(Integer id, HttpServletRequest request);

    EventFullDto updateEventByIdAdmin(Integer eventId, UpdateEventAdminRequest dto);

    List<EventShortDto> getAllEventsByUserId(Integer userId, int from, int size);

    EventFullDto addNewEvent(Integer userId, NewEventDto dto);

    EventFullDto getEventByUserId(Integer userId, Integer eventId);

    EventFullDto updateEventByUserId(Integer userId, Integer eventId, UpdateEventUserRequest dto);

    List<ParticipationRequestDto> getAllRequestsByUserAndEvent(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateEventRequestStatus(Integer userId, Integer eventId,
                                                            EventRequestStatusUpdateRequest request);
}
