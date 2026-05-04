package main.java.ru.practicum.presistence.repository;

import main.java.ru.practicum.presistence.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM categories
            WHERE categories.id = ANY(:ids)
            """)
    List<Category> getCategoriesByIds(@Param("ids") Long[] ids);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
