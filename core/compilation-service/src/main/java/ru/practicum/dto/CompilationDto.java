package main.java.ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import main.dto.EventShortDto;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

        List<EventShortDto> events;

        Long id;

        Boolean pinned;

        @NotBlank
        String title;
}
