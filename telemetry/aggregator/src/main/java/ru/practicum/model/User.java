package main.java.ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import main.java.ru.practicum.status.EventStatus;

import java.time.Instant;

@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
public class User {

    private int id;

    private double grade;

    private Instant instant;

    public User(int id, String status, Instant instant) {

        this.id = id;
        this.set(status, instant);
    }

    public void set(double grade, Instant instant) {

        if (this.grade < grade) {

            this.grade = grade;
            this.instant = instant;
        }
    }

    public void set(String status, Instant instant) {

        double getGrade = EventStatus.getValue(status);

        if (this.grade < getGrade) {

            this.grade = getGrade;
            this.instant = instant;
        }
    }
}
