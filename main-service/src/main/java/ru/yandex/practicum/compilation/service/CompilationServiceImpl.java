package ru.yandex.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.compilation.*;
import ru.yandex.practicum.compilation.repository.CompilationRepository;
import ru.yandex.practicum.event.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        if (pinned == null) {
            return compilationRepository.findAll(PageRequest.of(from, size)).getContent().stream()
                    .map(compilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }

        return compilationRepository.findAllByPinned(pinned, PageRequest.of(from, size))
                .getContent().stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка не найдена"));
        return compilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        if (null != dto.getPinned()) {
            compilation.setPinned(dto.getPinned());
        } else {
            compilation.setPinned(false);
        }
        List<Integer> eventsId = dto.getEvents();
        if (eventsId != null) {
            List<Event> events = eventRepository.findAllById(eventsId);
            compilation.setEvents(events);
        }
        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(savedCompilation);
    }

    @Override
    public void deleteCompilation(Integer compId) {
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest compilation) {
        Compilation compilationFromDb = compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Подборка не найдена"));
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            List<Integer> eventIds = compilation.getEvents();
            List<Event> events = eventRepository.findAllById(eventIds);
            compilationFromDb.setEvents(events);
        }
        if (compilation.getPinned() != null) {
            compilationFromDb.setPinned(compilation.getPinned());
        }
        if (compilation.getTitle() != null) {
            compilationFromDb.setTitle(compilation.getTitle());
        }
        Compilation updated = compilationRepository.save(compilationFromDb);
        return compilationMapper.toCompilationDto(updated);
    }
}