package ru.yandex.practicum.category.sevice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.category.Category;
import ru.yandex.practicum.category.CategoryDto;
import ru.yandex.practicum.category.CategoryMapper;
import ru.yandex.practicum.category.NewCategoryDto;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
            return categoryRepository.findAll(PageRequest.of(from, size)).getContent().stream()
                    .map(categoryMapper::toCategoryDto)
                    .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Integer catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория не найдена"));
        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsCategoryByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория уже существует");
        }
        if (newCategoryDto.getName().isBlank()) {
            throw new ValidationException("Имя категории не может быть пустым");
        }
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public void deleteCategory(Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Категория не найдена");
        }
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Событие еще не удалено");
        }
        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Integer catId, NewCategoryDto dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new ValidationException("Название категории не может быть пустым.");
        }
        Category updateCategory = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Категория не найдена"));
        if (!updateCategory.getName().equals(dto.getName()) &&
                categoryRepository.existsCategoryByName(dto.getName())) {
            throw new ConflictException("Категория уже существует");
        }
        updateCategory.setName(dto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(updateCategory));
    }
}
