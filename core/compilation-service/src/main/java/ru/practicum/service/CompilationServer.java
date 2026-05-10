package main.java.ru.practicum.service;

import main.java.ru.practicum.dto.ResponseCompilationDto;
import main.java.ru.practicum.dto.RequestCompilationDto;
import main.java.ru.practicum.dto.RequestUpdateCompilationDto;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CompilationServer {

    ResponseEntity<List<ResponseCompilationDto>> getCompilations(Boolean pinned, Integer from, Integer size);

    ResponseEntity<ResponseCompilationDto> getCompilation(Long compId);

    ResponseEntity<ResponseCompilationDto> saveCompilation(RequestCompilationDto newCompilationDto);

    ResponseEntity<Void> deleteCompilation(Long compId);

    ResponseEntity<ResponseCompilationDto> updateCompilation(Long compId, RequestUpdateCompilationDto updateCompilationRequest);
}
