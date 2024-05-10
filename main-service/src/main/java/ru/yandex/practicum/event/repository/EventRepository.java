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
            "where (lower(e.annotation) like lower(concat('%', :text, '%')) or lower(e.description) like lower(concat('%', :text, '%')) or :text is null) " +
            "and (e.category.id in :categories or :categories is null) " +
            "and (e.paid=:paid or :paid is null) " +
            "and (e.eventDate between :rangeStart and :rangeEnd) " +
            "and (e.state = :state) " +
            "order by e.eventDate")
    List<Event> findPublicEvent(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, EventState state, Pageable pageable);

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
}
