package ru.yandex.practicum.comment.service;

import ru.yandex.practicum.comment.CommentDto;
import ru.yandex.practicum.comment.CommentFullDto;

import javax.transaction.Transactional;
import java.util.List;

public interface CommentService {
    @Transactional
    CommentFullDto addComment(Integer userId, Integer eventId, CommentDto commentDto);

    @Transactional
    CommentFullDto updateComment(Integer commentId, Integer userId, CommentDto commentDto);

    void deleteCommentById(Integer commentId, Integer userId);

    void deleteCommentByAdmin(Integer commentId);

    @Transactional
    List<CommentFullDto> getAllCommentsByEventId(Integer eventId, int from, int size);
}
