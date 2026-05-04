package main.java.ru.practicum.mapper;

import main.java.ru.practicum.presistence.entity.Category;
import main.java.ru.practicum.dto.NewCategoryDto;
import main.java.ru.practicum.dto.CategoryDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    public Category toCategory(NewCategoryDto newCategoryDto);

    public CategoryDto toCategoryDto(Category category);
}
