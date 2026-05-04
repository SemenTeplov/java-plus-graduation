package main.java.ru.practicum.mapper;

import main.dto.LocationDto;
import main.java.ru.practicum.persistence.entity.Location;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto location);

    LocationDto locationToLocationDto(Location location);
}
