package ru.yandex.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    EndpointHit addHit(EndpointHit endpointHit);

    List<ViewStats> getViewStatsListWithoutUris(LocalDateTime start, LocalDateTime end, Boolean unique);

    List<ViewStats> getViewStatsListWithUris(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
