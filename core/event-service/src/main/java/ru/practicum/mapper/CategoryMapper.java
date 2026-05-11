package main.java.ru.practicum.mapper;

import main.java.ru.practicum.dto.RequestCategoryDto;
import main.java.ru.practicum.dto.ResponseCategoryDto;
import main.java.ru.practicum.persistence.entity.Category;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toCategory(RequestCategoryDto newCategoryDto);

    ResponseCategoryDto toCategoryDto(Category category);
}
