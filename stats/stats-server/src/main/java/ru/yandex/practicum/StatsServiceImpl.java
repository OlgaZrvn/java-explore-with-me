package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final HitMapper hitMapper;

    @Transactional
    @Override
    public EndpointHit addHit(EndpointHit endpointHit) {
        Hit hit = hitMapper.toHit(endpointHit);
        Hit returnedHits = statsRepository.save(hit);
        EndpointHit returnedEndpointHit = hitMapper.toHitDto(returnedHits);
        log.info("Добавлена новая запись с id = {}", returnedHits.getId());
        return returnedEndpointHit;
    }

    @Override
    public List<ViewStats> getViewStatsListWithoutUris(LocalDateTime start, LocalDateTime end, Boolean unique) {
        List<ViewStats> viewStatsList;
        if (unique) {
            viewStatsList = statsRepository.getViewStatsWithUniqueTrue(start, end);
        } else {
            viewStatsList = statsRepository.getViewStatsWithUniqueFalse(start, end);
        }
        if (viewStatsList.isEmpty()) {
            log.info("Список со статистикой просмотров для указанных параметров пуст.");
            return new ArrayList<>();
        }
        log.info("Список статистики просмотров с параметром unique = {} размером {} возвращён.", unique, viewStatsList.size());
        return viewStatsList;
    }

    @Override
    public List<ViewStats> getViewStatsListWithUris(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStats> viewStatsList;
        if (unique) {
            viewStatsList = statsRepository.getViewStatsWithUrisUniqueTrue(uris, start, end);
        } else {
            viewStatsList = statsRepository.getViewStatsWithUrisUniqueFalse(uris, start, end);
        }
        if (viewStatsList.isEmpty()) {
            log.info("Список со статистикой просмотров для указанных параметров пуст.");
            return new ArrayList<>();
        }
        log.info("Список статистики просмотров с параметром unique = {} размером {} возвращён.", unique, viewStatsList.size());
        return viewStatsList;
    }
}
