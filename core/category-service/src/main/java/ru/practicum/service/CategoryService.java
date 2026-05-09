package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.ResponseCategoryDto;
import main.java.ru.practicum.dto.RequestCategoryDto;

import java.util.List;

public interface CategoryService {

    ResponseCategoryDto addCategory(RequestCategoryDto requestCategoryDto);

    ResponseCategoryDto updateCategory(Long categoryId, ResponseCategoryDto categoryDto);

    List<ResponseCategoryDto> getCategories(Integer from, Integer size);

    ResponseCategoryDto getCategoryById(Long categoryId);

    void deleteCategory(Long categoryId);

    List<ResponseCategoryDto> getCategoriesByIds(List<Long> ids);
}
