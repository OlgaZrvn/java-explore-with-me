package ru.yandex.practicum;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {
    private StatsService statsService;

    @Mock
    StatsRepository statsRepository;

    @Mock
    HitMapper hitMapper;

    private final LocalDateTime start = LocalDateTime.now().minusMonths(12);

    private final LocalDateTime end = LocalDateTime.now().minusMonths(11);

    private final LocalDateTime wrongEnd = LocalDateTime.now().minusMonths(13);

    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        statsService = new StatsServiceImpl(statsRepository, hitMapper);
    }

    @Test
    void shouldSaveNewHit() {
        Hit hit = generator.nextObject(Hit.class);
        when(statsRepository.save(Mockito.any())).thenReturn(hit);
        EndpointHit endpointHit = hitMapper.toHitDto(hit);
        EndpointHit savedHit = statsService.addHit(endpointHit);
        assertEquals(endpointHit, savedHit);
    }

    @Test
    void shouldGet2ViewStats() {
        List<ViewStats> viewStatsList = List.of(generator.nextObject(ViewStats.class),
                generator.nextObject(ViewStats.class));
        when(statsRepository.getViewStatsWithUniqueTrue(Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)))
                .thenReturn(viewStatsList);
        List<ViewStats> savedViewStats = statsService.getViewStatsListWithoutUris(
                start, end, true);
        assertEquals(2, savedViewStats.size());
    }
}