package main.java.ru.practicum.model;

import lombok.Getter;
import lombok.ToString;

import main.java.ru.practicum.status.EventStatus;

import java.time.Instant;

@Getter
@ToString
public class User {

    private final int id;

    private volatile double grade;

    private Instant instant;

    public User(int id, String status, Instant instant) {

        this.id = id;
        this.set(status, instant);
    }

    public synchronized void set(double grade, Instant instant) {

        if (this.grade < grade) {

            this.grade = grade;
            this.instant = instant;
        }
    }

    public synchronized void set(String status, Instant instant) {

        double getGrade = EventStatus.getValue(status);

        if (this.grade < getGrade) {

            this.grade = getGrade;
            this.instant = instant;
        }
    }
}
