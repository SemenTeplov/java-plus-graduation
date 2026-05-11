package main.java.ru.practicum.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(of = {"id", "text", "authorId", "eventId", "created"})
@EqualsAndHashCode(of = {"id", "text", "authorId", "eventId", "created"})
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "text", length = 3000, nullable = false)
    String text;

    @Column(name = "author_id", nullable = false)
    Long authorId;

    @Column(name = "event_id", nullable = false)
    Long eventId;

    @Column(name = "created", nullable = false)
    LocalDateTime created;

    LocalDateTime edited;
}
