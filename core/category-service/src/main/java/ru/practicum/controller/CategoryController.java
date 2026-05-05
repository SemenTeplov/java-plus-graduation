package main.java.ru.practicum.controller;

import jakarta.validation.Valid;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.dto.CategoryDto;
import main.java.ru.practicum.dto.NewCategoryDto;
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
    ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(newCategoryDto));
    }

    @DeleteMapping("/admin/categories/{catId}")
    ResponseEntity<Void> deleteCategory(@PathVariable("catId") Long catId) {

        categoryService.deleteCategory(catId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    ResponseEntity<List<CategoryDto>> getCategories(@RequestParam Integer from, @RequestParam Integer size) {

        return ResponseEntity.ok(categoryService.getCategories(from, size));
    }

    @GetMapping("/categories/{catId}")
    ResponseEntity<CategoryDto> getCategory(@PathVariable("catId") Long catId) {

        return ResponseEntity.ok(categoryService.getCategoryById(catId));
    }

    @PatchMapping("/admin/categories/{catId}")
    ResponseEntity<CategoryDto> updateCategory(@PathVariable("catId") Long catId,
                                               @Valid @RequestBody CategoryDto categoryDto) {

        return ResponseEntity.ok(categoryService.updateCategory(catId, categoryDto));
    }

    @GetMapping("/categories/client")
    ResponseEntity<List<CategoryDto>> getCategoriesByIds(@RequestBody List<Long> ids) {

        return ResponseEntity.ok(categoryService.getCategoriesByIds(ids));
    }
}
