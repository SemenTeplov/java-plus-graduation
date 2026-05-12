package main.java.ru.practicum.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.dto.RequestCategoryDto;
import main.java.ru.practicum.dto.ResponseCategoryDto;
import main.java.ru.practicum.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    ResponseEntity<ResponseCategoryDto> addCategory(@Valid @RequestBody RequestCategoryDto requestCategoryDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(requestCategoryDto));
    }

    @DeleteMapping("/admin/categories/{catId}")
    ResponseEntity<Void> deleteCategory(@PathVariable("catId") Long catId) {

        categoryService.deleteCategory(catId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    ResponseEntity<List<ResponseCategoryDto>> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                            @RequestParam(required = false, defaultValue = "10") Integer size) {

        return ResponseEntity.ok(categoryService.getCategories(from, size));
    }

    @GetMapping("/categories/{catId}")
    ResponseEntity<ResponseCategoryDto> getCategory(@PathVariable("catId") Long catId) {

        return ResponseEntity.ok(categoryService.getCategoryById(catId));
    }

    @PatchMapping("/admin/categories/{catId}")
    ResponseEntity<ResponseCategoryDto> updateCategory(@PathVariable("catId") Long catId,
                                                       @Valid @RequestBody ResponseCategoryDto categoryDto) {

        return ResponseEntity.ok(categoryService.updateCategory(catId, categoryDto));
    }
}
