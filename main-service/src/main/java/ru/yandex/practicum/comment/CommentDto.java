package ru.yandex.practicum.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @Size(min = 10, max = 2000, message = "Размер комментария от 10 до 2000 символов")
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
