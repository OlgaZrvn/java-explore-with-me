package ru.yandex.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.EndpointHit;
import ru.yandex.practicum.StatsClient;
import ru.yandex.practicum.event.EventFullDto;
import ru.yandex.practicum.event.EventShortDto;
import ru.yandex.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/events")
public class EventController {

    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> getEventByFilter(@RequestParam(required = false, defaultValue = "") String text,
                                                @RequestParam(required = false) List<Integer> categories,
                                                @RequestParam(required = false) Boolean paid,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size,
                                                HttpServletRequest request) {

        List<EventShortDto> events = eventService.getEventByFilter(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from / size, size);
        statsClient.addHit(new EndpointHit("main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Integer id, HttpServletRequest request) {
        EventFullDto event = eventService.getPublicEventById(id);
        statsClient.addHit(new EndpointHit("main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        return event;
    }
}
