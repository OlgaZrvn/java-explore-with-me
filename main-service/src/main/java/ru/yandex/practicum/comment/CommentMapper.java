package ru.yandex.practicum.comment;

import ru.yandex.practicum.event.Event;
import ru.yandex.practicum.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, User user, Event event) {
        Comment comment = new Comment();
        comment.setCreatedOn(LocalDateTime.now());
        comment.setText(commentDto.getText());
        comment.setAuthor(user);
        comment.setEvent(event);
        return comment;
    }

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return new CommentFullDto(
                comment.getId(),
                comment.getCreatedOn(),
                comment.getEvent().getId(),
                comment.getAuthor().getId(),
                comment.getText()
        );
    }
}
