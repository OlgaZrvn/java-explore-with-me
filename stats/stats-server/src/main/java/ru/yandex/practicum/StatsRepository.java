package ru.yandex.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query(value = "SELECT new ru.yandex.practicum.ViewStats (h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.date BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> getViewStatsWithUniqueTrue(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.yandex.practicum.ViewStats (h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.date BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> getViewStatsWithUniqueFalse(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.yandex.practicum.ViewStats (h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.uri IN :uris " +
            "AND h.date BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> getViewStatsWithUrisUniqueTrue(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.yandex.practicum.ViewStats (h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.uri IN :uris " +
            "AND h.date BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStats> getViewStatsWithUrisUniqueFalse(List<String> uris, LocalDateTime start, LocalDateTime end);
}
