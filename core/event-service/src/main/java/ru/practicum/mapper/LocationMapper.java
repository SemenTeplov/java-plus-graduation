package main.java.ru.practicum.mapper;

import main.dto.LocationDto;
import main.java.ru.practicum.persistence.entity.Location;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto location);

    LocationDto locationToLocationDto(Location location);
}
