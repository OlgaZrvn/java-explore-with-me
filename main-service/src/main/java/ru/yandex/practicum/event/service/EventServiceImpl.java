package ru.yandex.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.StatsClient;
import ru.yandex.practicum.ViewStats;
import ru.yandex.practicum.category.Category;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.event.*;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.location.repository.LocationRepository;
import ru.yandex.practicum.request.*;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.request.RequestStatus.CONFIRMED;
import static ru.yandex.practicum.request.RequestStatus.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEventByFilter(String text, List<Integer> categories, Boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                Boolean onlyAvailable, String sort, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException("Дата окончания не может быть раньше даты начала");
            }
        }
        LocalDateTime dateStartSearch = LocalDateTime.now().plusSeconds(1L);
        LocalDateTime dateEndSearch = LocalDateTime.now().plusYears(99L);
        if (rangeStart != null) {
            dateStartSearch = rangeStart;
        }
        if (rangeEnd != null) {
            dateEndSearch = rangeEnd;
        }
        if (categories == null || categories.isEmpty()) {
            categories = categoryRepository.findAll()
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toList());
        }
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findPublicEvent(
                text, categories, paid, dateStartSearch, dateEndSearch, EventState.PUBLISHED, pageable);
        if (onlyAvailable) {
            events = events
                    .stream()
                    .filter(e -> e.getParticipantLimit() > getConfirmedRequests(e.getId()))
                    .collect(Collectors.toList());
        }
        LocalDateTime start = dateStartSearch;
        LocalDateTime end = dateEndSearch;
        List<EventShortDto> eventShorts = events
                .stream()
                .map(eventMapper::toEventShortDto)
                .peek(e -> {
                    e.setViews(viewsEvent(start, end, "/events/" + e.getId(), false));
                })
                .collect(Collectors.toList());
        if (sort.equals("VIEWS")) {
            eventShorts.sort(Comparator.comparing(EventShortDto::getViews));
        }
        return eventShorts;
    }

    private Long getConfirmedRequests(Integer eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
    }

    private Long viewsEvent(LocalDateTime rangeStart, LocalDateTime rangeEnd, String uris, Boolean unique) {
        List<?> body = Collections.singletonList(statsClient.getViewStats(rangeStart, rangeEnd, List.of(uris), unique));
        return body.size() > 0 ? ((ViewStats) body.get(0)).getHits() : 1L;
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException("Дата окончания не может быть раньше даты начала");
            }
        }
        rangeStart = rangeStart == null ? LocalDateTime.now() : rangeStart;
        List<Event> events = eventRepository.findAllForAdmin(users, states, categories, rangeStart,
                PageRequest.of(from, size, Sort.unsorted()));
        setConfirmedRequests(events);
        return events.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
    }

    private void setConfirmedRequests(List<Event> events) {
        List<Integer> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        List<ConfirmedRequest> confirmedRequests = requestRepository.findConfirmedRequest(eventIds);
        Map<Integer, Long> confirmedRequestsMap = confirmedRequests
                .stream()
                .collect(Collectors.toMap(ConfirmedRequest::getEventId, ConfirmedRequest::getCount));
        events.forEach(event -> event.setConfirmedRequests(
                Math.toIntExact(confirmedRequestsMap.getOrDefault(event.getId(), Long.valueOf(0)))));
    }

    @Override
    @Transactional
    public EventFullDto getPublicEventById(Integer id) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Событие не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано");
        }
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        Long confirmedRequests = getConfirmedRequests(event.getId());
        Long views = viewsEvent(LocalDateTime.now().plusSeconds(1L),
                LocalDateTime.now().plusYears(99L), "/events/" + event.getId(), false);
        eventFullDto.setViews(views);
        eventFullDto.setConfirmedRequests(confirmedRequests);
        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByIdAdmin(Integer eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие не найдено"));

        updateEventCommonFields(event, eventDto);

        if (eventDto.getStateAction() != null) {
            if (event.getState().equals(EventState.PENDING)) {
                if (eventDto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                    event.setState(EventState.CANCELED);
                }
                if (eventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
            } else {
                throw new ConflictException("Состояние неверное: " + event.getState());
            }
        }

        if (eventDto.getEventDate() != null && event.getState().equals(EventState.PUBLISHED)) {
            if (eventDto.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
                event.setEventDate(eventDto.getEventDate());
            } else {
                throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            }
        }
        event = eventRepository.save(event);
        locationRepository.save(event.getLocation());
        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    private void updateEventCommonFields(Event event, UpdateEventAdminRequest eventDto) {
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
            if (eventDto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
        }
        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank()) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            if ((eventDto.getParticipantLimit() < 0)) {
                throw new ConflictException("Лимит превышен");
            }
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null && !eventDto.getTitle().isBlank()) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }
        if (eventDto.getEventDate() != null) {
            if (eventDto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
            } else {
                event.setEventDate(eventDto.getEventDate());
            }
        }
    }

    @Override
    public List<EventShortDto> getAllEventsByUserId(Integer userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size))
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto addNewEvent(Integer userId, NewEventDto dto) {
        if (dto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> new NotFoundException("Категория не найдена"));
        Event event = EventMapper2.toEventFromNewEventDto(dto, user, category);

        if (dto.getParticipantLimit() < 0) {
            throw new ValidationException("Лимит участников отрицательный");
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByUserId(Integer userId, Integer eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException("Событие не найдено"));
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUserId(Integer userId, Integer eventId, UpdateEventUserRequest dto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotFoundException("Событие не найдено"));

        if (event.getState() == EventState.PUBLISHED || event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Статус CANCELED или PENDING");
        }
        updateEventCommonFieldsByUser(event, dto);
        Event eventSaved = eventRepository.save(event);
        locationRepository.save(eventSaved.getLocation());
        return eventMapper.toEventFullDto(eventSaved);
    }

    @Transactional
    private void updateEventCommonFieldsByUser(Event event, UpdateEventUserRequest eventDto) {
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
            if (eventDto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
        }
        if (eventDto.getAnnotation() != null && !eventDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank()) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            if ((eventDto.getParticipantLimit() < 0)) {
                throw new ValidationException("Лимит участников отрицательный");
            }
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (eventDto.getTitle() != null && !eventDto.getTitle().isBlank()) {
            event.setTitle(eventDto.getTitle());
        }
        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }
        if (eventDto.getEventDate() != null) {
            if (eventDto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
            } else {
                event.setEventDate(eventDto.getEventDate());
            }
        }
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsByUserAndEvent(Integer userId, Integer eventId) {
        if (eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            return requestRepository.findAllByEventId(eventId)
                    .stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(Integer userId,
                                                                   Integer eventId,
                                                                   EventRequestStatusUpdateRequest request) {
        RequestStatus status = request.getStatus();
        if (status == CONFIRMED || status == REJECTED) {
            Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                    () -> new NotFoundException("Событие не найдено"));
            if (!event.getInitiator().getId().equals(userId)) {
                throw new ConflictException("Пользователь не может обновлять запросы к событию, автором которого он не является");
            }
            Integer participantLimit = event.getParticipantLimit();
            if (!event.getRequestModeration()) {
                throw new ConflictException("Событию не нужна модерация");
            }
            Long numberOfParticipants = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
            if ((numberOfParticipants >= participantLimit) || (participantLimit == 0)) {
                throw new ConflictException("В событии уже максимальное кол-во участников");
            }
            List<Request> requests = requestRepository.findByIdIn(request.getRequestIds());
            RequestStatus newStatus = request.getStatus();
            for (Request request1 : requests) {
                if (request1.getEvent().getId().equals(eventId)) {
                    if (participantLimit > numberOfParticipants) {
                        if (newStatus == CONFIRMED && request1.getStatus() != CONFIRMED) {
                            numberOfParticipants++;
                        }
                        request1.setStatus(newStatus);
                    } else {
                        request1.setStatus(REJECTED);
                    }
                } else {
                    throw new ConflictException("Запрос и событие не совпадают");
                }
            }
            requestRepository.saveAll(requests);
            List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, CONFIRMED);
            List<Request> rejectedRequests = requestRepository.findAllByEventIdAndStatus(eventId, REJECTED);

            List<ParticipationRequestDto> confirmedRequestDtos = confirmedRequests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());

            List<ParticipationRequestDto> rejectedRequestDtos = rejectedRequests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(confirmedRequestDtos, rejectedRequestDtos);
        } else {
            throw new ConflictException("Доступны только статусы CONFIRMED или REJECTED");
        }
    }
}
