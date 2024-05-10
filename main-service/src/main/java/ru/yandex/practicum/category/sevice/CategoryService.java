package ru.yandex.practicum.category.sevice;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Integer catId);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Integer catId);

    @Transactional
    CategoryDto updateCategory(Integer catId, NewCategoryDto dto);
}