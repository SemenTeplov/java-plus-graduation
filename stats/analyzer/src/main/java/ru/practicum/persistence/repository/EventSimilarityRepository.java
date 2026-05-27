package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.model.EventSimilarity;
import main.java.ru.practicum.persistence.model.EventSimilarityId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, EventSimilarityId> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM event_similarities
            WHERE event_a_id = :id OR event_b_id = :id""")
    List<EventSimilarity> getEventsByEventId(@Param("id") int id);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM event_similarities
            WHERE (event_a_id = :id OR event_b_id = :id)
            AND (event_a_id IN :ids OR event_b_id IN :ids)
            ORDER BY score DESC
            LIMIT :count""")
    List<EventSimilarity> getEventsByEventIdAndUserIds(@Param("id") int id, @Param("ids") Long[] ids, @Param("count") int count);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM event_similarities
            WHERE (event_a_id IN (:id) AND event_b_id NOT IN (:id))
            OR (event_a_id NOT IN (:id) AND event_b_id IN (:id))""")
    List<EventSimilarity> getEventsByEventIds(@Param("id") Long[] id);
}
