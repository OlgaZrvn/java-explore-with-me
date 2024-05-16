package ru.yandex.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.CommentFullDto;
import ru.yandex.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentFullDto> getAllCommentsByEventId(@PathVariable("eventId") Integer eventId,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") int size)  {
        log.info("Запрос на получение всех комментариев");
        return commentService.getAllCommentsByEventId(eventId, from / size, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable("commentId") Integer commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }

}
