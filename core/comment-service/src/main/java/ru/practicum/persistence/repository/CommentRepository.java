package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.entity.Comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByAuthorId(Long authorId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);
}
