package ru.yandex.practicum.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.event.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    @NotNull
    private Integer id;
    @NotNull
    private Boolean pinned;
    @NotBlank(message = "Имя не может быть пустым")
    private String title;
    private List<EventShortDto> events;
}
