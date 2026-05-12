package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.entity.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM events
            WHERE initiator = :userId AND id = :eventId
            """)
    Optional<Event> getEventByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM events
            WHERE initiator = :userId
            LIMIT :size
            OFFSET :from
            """)
    List<Event> getEventsUser(@Param("userId") Long userId, @Param("from") Integer from, @Param("size") Integer size);

    @Query(nativeQuery = true, value = """
        SELECT e.*
        FROM events e
        WHERE e.id = ANY(:eventIds)
        """)
    List<Event> getEventsByIds(@Param("eventIds") Long[] eventIds);

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.category = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") Long categoryId);
}
