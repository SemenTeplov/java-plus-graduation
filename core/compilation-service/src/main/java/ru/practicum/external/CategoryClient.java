package main.java.ru.practicum.external;

import main.dto.CategoryDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "category-service")
public interface CategoryClient {

    @GetMapping("/categories/client")
    ResponseEntity<List<CategoryDto>> getCategoriesByIds(@RequestBody List<Long> ids);
}
