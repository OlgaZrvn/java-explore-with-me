package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHit addHit(@RequestBody @Valid EndpointHit endpointHit) {
        return statsService.addHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam (required = false) List<String> uris,
                                    @RequestParam (defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала должна быть раньше даты конца.");
        }
        if (uris == null) {
            return statsService.getViewStatsListWithoutUris(start, end, unique);
        } else {
            return statsService.getViewStatsListWithUris(start, end, uris, unique);
        }
    }
}
