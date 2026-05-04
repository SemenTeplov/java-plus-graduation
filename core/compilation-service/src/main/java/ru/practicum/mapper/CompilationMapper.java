package main.java.ru.practicum.mapper;

import main.java.ru.practicum.dto.CompilationDto;
import main.java.ru.practicum.dto.NewCompilationDto;
import main.java.ru.practicum.dto.UpdateCompilationRequest;
import main.java.ru.practicum.persistence.entity.Compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "events", ignore = true)
    CompilationDto compilationToCompilationDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto);

    @Mapping(target = "events", ignore = true)
    void updateCompilationRequestToCompilation(@MappingTarget Compilation compilation,
                                               UpdateCompilationRequest updateCompilationRequest);
}
