package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.CategoryDto;
import main.java.ru.practicum.dto.NewCategoryDto;
import main.java.ru.practicum.exception.ForbiddenException;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.external.EventClient;
import main.java.ru.practicum.mapper.CategoryMapper;
import main.java.ru.practicum.presistence.entity.Category;
import main.java.ru.practicum.presistence.repository.CategoryRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    private final EventClient eventClient;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {

        log.info(Messages.MESSAGE_ADD_CATEGORIES, newCategoryDto);

        if (categoryRepository.existsByName(newCategoryDto.name())) {
            throw new ForbiddenException(String.format(Exceptions.EXCEPTION_CONFLICT_CATEGORY, newCategoryDto.name()));
        }

        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {

        log.info(Messages.MESSAGE_UPDATE_CATEGORY, categoryId, categoryDto);

        Category category = getCategory(categoryId);

        if (!category.getName().equals(categoryDto.name())) {
            boolean nameExists = categoryRepository.existsByNameAndIdNot(categoryDto.name(), categoryId);

            if (nameExists) {
                throw new ForbiddenException(String.format(Exceptions.EXCEPTION_CONFLICT_CATEGORY, categoryDto.name()));
            }
        }

        category.setName(categoryDto.name());

        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {

        log.info(Messages.MESSAGE_GET_CATEGORIES);

        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(categoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {

        return categoryMapper.toCategoryDto(getCategory(categoryId));
    }

    @Override
    public void deleteCategory(Long categoryId) {

        log.info(Messages.MESSAGE_DELETE_CATEGORIES, categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format(Messages.MESSAGE_CATEGORY_NOT_FOUND, categoryId));
        }

        if (Boolean.TRUE.equals(eventClient.existsByCategoryId(categoryId).getBody())) {
            throw new ForbiddenException(Exceptions.EXCEPTION_CANT_DELETE_CATEGORY);
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getCategoriesByIds(List<Long> ids) {

        return categoryRepository.getCategoriesByIds(ids.toArray(Long[]::new)).stream()
                .map(categoryMapper::toCategoryDto).toList();
    }

    private Category getCategory(Long categoryId) {

        log.info(Messages.MESSAGE_GET_CATEGORY, categoryId);

        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(Messages.MESSAGE_CATEGORY_NOT_FOUND, categoryId)));
    }
}
