package main.java.ru.practicum.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(of = {"id", "pinned", "events"})
@EqualsAndHashCode(of = {"id", "pinned", "events"})
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "pinned", nullable = false)
    Boolean pinned;

    String title;

    @ElementCollection
    @CollectionTable(
            name = "compilation_to_events",
            joinColumns = @JoinColumn(name = "compilation_id"))
    @Column(name = "event_id")
    List<Long> events = new ArrayList<>();
}
