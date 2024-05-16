package ru.yandex.practicum.comment;

public class CommentMapper2 {

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
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
