package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.model.UserAction;
import main.java.ru.practicum.persistence.model.UserActionId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActionRepository extends JpaRepository<UserAction, UserActionId> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM user_actions
            WHERE user_id = :id""")
    List<UserAction> getUsersById(@Param("id") int id);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM user_actions
            WHERE user_id <> :id""")
    List<UserAction> getUsersWithoutId(@Param("id") int id);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM user_actions
            WHERE event_id = :id""")
    List<UserAction> getUsersByEventId(@Param("id") int id);
}
