package ru.yandex.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.CommentDto;
import ru.yandex.practicum.comment.CommentFullDto;
import ru.yandex.practicum.comment.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto addComment(@PathVariable("userId") Integer userId,
                                     @RequestParam Integer eventId,
                                     @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на создание комментария");
        return commentService.addComment(userId, eventId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable("userId") Integer userId,
                                  @PathVariable("commentId") Integer commentId) {
        log.info("Запрос на удаление комментария");
        commentService.deleteCommentById(commentId, userId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto updateComment(@PathVariable("userId") Integer userId,
                                        @PathVariable("commentId") Integer commentId,
                                        @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на изменение комментария");
        return commentService.updateComment(commentId, userId, commentDto);
    }
}
