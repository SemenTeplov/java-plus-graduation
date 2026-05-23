package main.java.ru.practicum.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import main.java.ru.practicum.persistence.status.ActionType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_actions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class UserAction {

    @EmbeddedId
    UserActionId userActionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    ActionType actionType;

    @Column(name = "timestamp_ms", nullable = false)
    LocalDateTime timestampMs;
}