package ru.yandex.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.*;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEventsByUserId(@PathVariable Integer userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getAllEventsByUserId(userId, from / size, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@PathVariable Integer userId,
                                    @Valid @RequestBody NewEventDto dto) {
        log.info("Запрос на создание события");
        return eventService.addNewEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserId(@PathVariable("userId") Integer userId,
                                         @PathVariable("eventId") Integer eventId) {
        return eventService.getEventByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUserId(@PathVariable("userId") Integer userId,
                                            @PathVariable("eventId") Integer eventId,
                                            @Valid @RequestBody UpdateEventUserRequest event) {
        return eventService.updateEventByUserId(userId, eventId, event);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsByUserAndEvent(@PathVariable("userId") Integer userId,
                                                                      @PathVariable("eventId") Integer eventId) {
        return eventService.getAllRequestsByUserAndEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestStatus(@PathVariable("userId") Integer userId,
                                                                   @PathVariable("eventId") Integer eventId,
                                                                   @RequestBody EventRequestStatusUpdateRequest request) {
        return eventService.updateEventRequestStatus(userId, eventId, request);
    }
}
