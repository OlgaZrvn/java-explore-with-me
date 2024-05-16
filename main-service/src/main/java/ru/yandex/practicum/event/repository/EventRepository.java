package ru.yandex.practicum.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.event.Event;
import ru.yandex.practicum.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("select e " +
            "from Event e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.category " +
            "where e.eventDate > :rangeStart " +
            "and (e.category.id in :categories or :categories is null) " +
            "and (e.initiator.id in :users or :users is null) " +
            "and (e.state in :states or :states is null)")
    List<Event> findAllForAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
                                LocalDateTime rangeStart, PageRequest pageRequest);

    Boolean existsByCategoryId(Integer categoryId);

    Optional<Event> findByIdAndInitiatorId(Integer id, Integer userId);

    Boolean existsByIdAndInitiatorId(Integer id, Integer userId);

    List<Event> findAllByInitiatorId(Integer userId, PageRequest of);

    @Query("select e " +
            "FROM Event e " +
            "WHERE (e.state = 'PUBLISHED') " +
            "AND (upper(e.annotation) like upper(concat('%', :text, '%')) OR upper(e.description) like upper(concat('%', :text, '%')) " +
            "OR :text IS null) " +
            "AND (e.category.id IN :categories OR :categories IS null) " +
            "AND (e.paid = :paid OR :paid IS null) " +
            "AND (e.eventDate > :rangeStart OR CAST(:rangeStart AS LocalDateTime) IS null) " +
            "AND (e.eventDate < :rangeEnd OR CAST(:rangeEnd AS LocalDateTime) IS null) " +
            "AND (e.confirmedRequests <= e.participantLimit)")
    List<Event> findAllByFilterIsAvailable(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e " +
            "FROM Event e " +
            "WHERE (e.state = 'PUBLISHED') " +
            "AND (upper(e.annotation) like upper(concat('%', :text, '%')) OR upper(e.description) like upper(concat('%', :text, '%')) " +
            "OR :text IS null) " +
            "AND (e.category.id IN :categories OR :categories IS null) " +
            "AND (e.paid = :paid OR :paid IS null) " +
            "AND (e.eventDate > :rangeStart OR CAST(:rangeStart AS LocalDateTime) IS null) " +
            "AND (e.eventDate < :rangeEnd OR CAST(:rangeEnd AS LocalDateTime) IS null)")
    List<Event> findAllByFilterNotAvailable(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.id = :id " +
            "AND (e.state = 'PUBLISHED')")
    Optional<Event> findByIdOnlyPublic(Integer id);
}
