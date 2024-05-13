package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam (required = false) List<String> uris,
                                    @RequestParam (defaultValue = "false") Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Дата начала должна быть раньше даты конца.");
        }
        if (uris == null) {
            return statsService.getViewStatsListWithoutUris(startTime, endTime, unique);
        } else {
            return statsService.getViewStatsListWithUris(startTime, endTime, uris, unique);
        }
    }
}
