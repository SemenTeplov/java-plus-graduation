package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.dto.RequestCategoryDto;
import main.java.ru.practicum.dto.ResponseCategoryDto;
import main.java.ru.practicum.exception.ForbiddenException;
import main.java.ru.practicum.exception.NotFoundException;
import main.java.ru.practicum.mapper.CategoryMapper;
import main.java.ru.practicum.persistence.entity.Category;

import main.java.ru.practicum.persistence.repository.CategoryRepository;
import main.java.ru.practicum.persistence.repository.EventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public ResponseCategoryDto addCategory(RequestCategoryDto requestCategoryDto) {

        log.info(Messages.MESSAGE_ADD_CATEGORIES, requestCategoryDto);

        if (categoryRepository.existsByName(requestCategoryDto.name())) {
            throw new ForbiddenException(String.format(Exceptions.EXCEPTION_CONFLICT_CATEGORY, requestCategoryDto.name()));
        }

        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(requestCategoryDto)));
    }

    @Override
    public ResponseCategoryDto updateCategory(Long categoryId, ResponseCategoryDto categoryDto) {

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
    public List<ResponseCategoryDto> getCategories(Integer from, Integer size) {

        log.info(Messages.MESSAGE_GET_CATEGORIES);

        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(categoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public ResponseCategoryDto getCategoryById(Long categoryId) {

        return categoryMapper.toCategoryDto(getCategory(categoryId));
    }

    @Override
    public void deleteCategory(Long categoryId) {

        log.info(Messages.MESSAGE_DELETE_CATEGORIES, categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format(Messages.MESSAGE_CATEGORY_NOT_FOUND, categoryId));
        }

        if (Boolean.TRUE.equals(eventRepository.existsByCategoryId(categoryId))) {
            throw new ForbiddenException(Exceptions.EXCEPTION_CANT_DELETE_CATEGORY);
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<ResponseCategoryDto> getCategoriesByIds(List<Long> ids) {

        log.info(Messages.MESSAGE_GET_CATEGORIES_BY_IDS, ids);

        return categoryRepository.getCategoriesByIds(ids.toArray(Long[]::new)).stream()
                .map(categoryMapper::toCategoryDto).toList();
    }

    private Category getCategory(Long categoryId) {

        log.info(Messages.MESSAGE_GET_CATEGORY, categoryId);

        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(Messages.MESSAGE_CATEGORY_NOT_FOUND, categoryId)));
    }
}
