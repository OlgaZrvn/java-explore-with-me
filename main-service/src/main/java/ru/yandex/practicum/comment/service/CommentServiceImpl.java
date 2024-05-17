package ru.yandex.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.comment.Comment;
import ru.yandex.practicum.comment.CommentDto;
import ru.yandex.practicum.comment.CommentFullDto;
import ru.yandex.practicum.comment.CommentMapper;
import ru.yandex.practicum.comment.repository.CommentRepository;
import ru.yandex.practicum.event.Event;
import ru.yandex.practicum.event.EventState;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.request.Request;
import ru.yandex.practicum.request.RequestStatus;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public CommentFullDto addComment(Integer userId, Integer eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие еще не опубликовано");
        }
        if (!Objects.equals(user.getId(), event.getInitiator().getId())) {
            List<Request> requests = requestRepository.findAllByEventIdAndStatusAndRequesterId(eventId,
                    RequestStatus.CONFIRMED, userId);
            if (requests.isEmpty()) {
                throw new ConflictException("Пользватель не участник и не автор события");
            }
        }
        if (commentRepository.existsCommentByEventIdAndAuthorId(eventId, userId)) {
            throw new ConflictException("Комментарий уже существует");
        }
        Comment comment = CommentMapper.toComment(commentDto, user, event);
        CommentFullDto savedComment = CommentMapper.toCommentFullDto(commentRepository.save(comment));
        log.info("Создан комментарий с id = {}", savedComment.getId());
        return savedComment;
    }

    @Override
    @Transactional
    public CommentFullDto updateComment(Integer commentId, Integer userId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий не найден"));
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new ConflictException("Пользователь не является автором комментария");
        }
        String newText = commentDto.getText();
        if (newText != null && !newText.isEmpty()) {
            comment.setText(newText);
        }
        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    public void deleteCommentById(Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий не найден"));
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new ConflictException("Пользователь не является автором комментария");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentByAdmin(Integer commentId) {
        commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий не найден"));
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public List<CommentFullDto> getAllCommentsByEventId(Integer eventId, int from, int size) {
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
        List<Comment> comments = commentRepository.findAllByEventIdOrderByCreatedOnDesc(eventId,
                PageRequest.of(from, size));
        return comments.stream().map(CommentMapper::toCommentFullDto).collect(Collectors.toList());
    }
}
