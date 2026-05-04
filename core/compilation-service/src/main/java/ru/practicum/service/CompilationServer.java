package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.CompilationDto;
import main.java.ru.practicum.dto.NewCompilationDto;
import main.java.ru.practicum.dto.UpdateCompilationRequest;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CompilationServer {

    ResponseEntity<List<CompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size);

    ResponseEntity<CompilationDto> getCompilation(Long compId);

    ResponseEntity<CompilationDto> saveCompilation(NewCompilationDto newCompilationDto);

    ResponseEntity<Void> deleteCompilation(Long compId);

    ResponseEntity<CompilationDto> updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
