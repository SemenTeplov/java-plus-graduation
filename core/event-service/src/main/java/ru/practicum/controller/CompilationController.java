package main.java.ru.practicum.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.dto.RequestCompilationDto;
import main.java.ru.practicum.dto.RequestUpdateCompilationDto;
import main.java.ru.practicum.dto.ResponseCompilationDto;
import main.java.ru.practicum.service.CompilationServer;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class CompilationController {

    private final CompilationServer compilationServer;

    @DeleteMapping("/admin/compilations/{compId}")
    ResponseEntity<Void> deleteCompilation(@PathVariable("compId") Long compId) {

        return compilationServer.deleteCompilation(compId);
    }

    @GetMapping("/compilations/{compId}")
    ResponseEntity<ResponseCompilationDto> getCompilation(@PathVariable("compId") Long compId) {

        return compilationServer.getCompilation(compId);
    }

    @GetMapping("/compilations")
    ResponseEntity<List<ResponseCompilationDto>> getCompilations(
            @RequestParam(value = "pinned", required = false) @Nullable Boolean pinned,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return compilationServer.getCompilations(pinned, from, size);
    }

    @PostMapping("/admin/compilations")
    ResponseEntity<ResponseCompilationDto> saveCompilation(@Valid @RequestBody RequestCompilationDto request) {

        return compilationServer.saveCompilation(request);
    }

    @PatchMapping("/admin/compilations/{compId}")
    ResponseEntity<ResponseCompilationDto> updateCompilation(@PathVariable("compId") Long compId,
                                                             @Valid @RequestBody RequestUpdateCompilationDto request) {

        return compilationServer.updateCompilation(compId, request);
    }
}
