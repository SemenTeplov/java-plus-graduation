package main.java.ru.practicum.mapper;

import main.java.ru.practicum.dto.EventShortDto;
import main.java.ru.practicum.dto.ResponseCompilationDto;
import main.java.ru.practicum.dto.RequestCompilationDto;
import main.java.ru.practicum.dto.RequestUpdateCompilationDto;
import main.java.ru.practicum.persistence.entity.Compilation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "events", source = "list")
    ResponseCompilationDto compilationAndEventsToCompilationDto(Compilation compilation, List<EventShortDto> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "list")
    Compilation newCompilationDtoToCompilation(RequestCompilationDto newCompilationDto, List<Long> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    void updateCompilationRequestToCompilation(@MappingTarget Compilation compilation,
                                               RequestUpdateCompilationDto updateCompilationRequest);
}
