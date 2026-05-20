package main.java.ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import main.java.ru.practicum.status.EventStatus;

@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
public class User {

    private int id;

    private double grade;

    public User(int id, String status) {

        this.id = id;
        this.set(status);
    }

    public boolean set(double grade) {

        if (this.grade < grade) {

            this.grade = grade;

            return true;
        }

        return false;
    }

    public boolean set(String status) {

        double getGrade = EventStatus.getValue(status);

        if (this.grade < getGrade) {

            this.grade = getGrade;

            return true;
        }

        return false;
    }
}
