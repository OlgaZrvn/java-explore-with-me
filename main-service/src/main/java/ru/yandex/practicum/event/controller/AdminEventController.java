package ru.yandex.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.EventFullDto;
import ru.yandex.practicum.event.EventState;
import ru.yandex.practicum.event.UpdateEventAdminRequest;
import ru.yandex.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllEventsAdmin(@RequestParam(required = false) List<Integer> users,
                                                @RequestParam(required = false) List<EventState> states,
                                                @RequestParam(required = false) List<Integer> categories,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from / size, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Integer eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest dto) {
        return eventService.updateEventByIdAdmin(eventId, dto);
    }
}
