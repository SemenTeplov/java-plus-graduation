package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.entity.Location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}
