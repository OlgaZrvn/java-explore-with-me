package ru.yandex.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private List<Integer> events;
    private Boolean pinned;
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 50)
    private String title;
}
