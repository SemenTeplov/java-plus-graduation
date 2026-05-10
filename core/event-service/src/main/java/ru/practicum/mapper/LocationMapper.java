package main.java.ru.practicum.mapper;

import main.java.ru.practicum.dto.LocationDto;
import main.java.ru.practicum.persistence.entity.Location;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    Location locationDtoToLocation(LocationDto location);

    LocationDto locationToLocationDto(Location location);
}
