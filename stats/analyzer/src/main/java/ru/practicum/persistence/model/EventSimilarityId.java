package main.java.ru.practicum.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class EventSimilarityId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "event_a_id", nullable = false)
    Long eventAId;

    @Column(name = "event_b_id", nullable = false)
    Long eventBId;
}
