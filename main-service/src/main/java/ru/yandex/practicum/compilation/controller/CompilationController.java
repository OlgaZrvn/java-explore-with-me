package ru.yandex.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.compilation.CompilationDto;
import ru.yandex.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/compilation")
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(compilationService.getAllCompilations(pinned, from, size));
    }

    @GetMapping("{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable("compId") Integer compId) {
        return ResponseEntity.ok().body(compilationService.getCompilationById(compId));
    }

}
