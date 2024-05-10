package ru.yandex.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.category.sevice.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                              @Positive @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(categoryService.getAllCategories(from, size));
    }

    @GetMapping("{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("catId") Integer catId) {
        return ResponseEntity.ok().body(categoryService.getCategoryById(catId));
    }
}
