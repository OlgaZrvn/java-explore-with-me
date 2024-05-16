package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.request.ConfirmedRequest;
import ru.yandex.practicum.request.Request;
import ru.yandex.practicum.request.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    @Query("select new ru.yandex.practicum.request.ConfirmedRequest(r.event.id, count(distinct r)) " +
            "from Request r " +
            "where r.status = 'CONFIRMED' and r.event.id in :eventsIds " +
            "group by r.event.id")
    List<ConfirmedRequest> findConfirmedRequest(List<Integer> eventsIds);

    Long countByEventIdAndStatus(Integer eventId, RequestStatus status);

    List<Request> findAllByEventId(Integer eventId);

    List<Request> findByIdIn(List<Integer> requestIds);

    List<Request> findAllByEventIdAndStatus(Integer eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Integer userId);

    List<Request> findAllByEventIdAndRequesterId(Integer eventId, Integer requesterId);

    List<Request> findAllByEventIdAndStatusAndRequesterId(Integer eventId, RequestStatus confirmed, Integer userId);
}
