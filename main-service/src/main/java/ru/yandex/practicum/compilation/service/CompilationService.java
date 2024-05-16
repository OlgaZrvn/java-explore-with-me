package ru.yandex.practicum.compilation.service;

import ru.yandex.practicum.compilation.CompilationDto;
import ru.yandex.practicum.compilation.NewCompilationDto;
import ru.yandex.practicum.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Integer compId);


    CompilationDto addCompilation(NewCompilationDto dto);

    void deleteCompilation(Integer compId);

    CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest compilation);
}
