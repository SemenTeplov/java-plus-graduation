package main.java.ru.practicum.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import main.java.ru.practicum.persistence.status.State;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of =
        {"id", "annotation", "category", "description", "eventDate", "location", "initiator", "paid",
                "participantLimit", "requestModeration", "title"})
@ToString(of =
        {"id", "annotation", "category", "description", "eventDate", "location", "initiator", "paid",
                "participantLimit", "requestModeration", "title"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation", nullable = false)
    String annotation;

    Integer confirmedRequests;

    @Column(name = "category", nullable = false)
    Long category;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "event_date", nullable = false)
    OffsetDateTime eventDate;

    @Column(name = "location", nullable = false)
    Long location;

    @Column(name = "initiator", nullable = false)
    Long initiator;

    @Column(name = "paid", nullable = false)
    Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    Integer participantLimit;

    @Column(name = "request_moderation", nullable = false)
    Boolean requestModeration;

    @Column(name = "title", nullable = false)
    String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 10)
    State state;

    Long views;
}
