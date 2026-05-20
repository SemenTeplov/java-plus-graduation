package main.java.ru.practicum.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Event {

    private int id;

    private final Map<Integer, User> users;

    private double sum;

    public Event(int id) {

        this.users = new HashMap<>();
        this.id = id;
    }

    public Event(int id, User user) {

        this(id);
        add(user);
    }

    public void add(User user) {

        if (users.containsKey(user.getId())) {

            User owner = users.get(user.getId());

            sum =- owner.getGrade();
            owner.set(user.getGrade());
            sum =+ owner.getGrade();

        } else {

            users.put(user.getId(), user);
            sum =+ user.getGrade();
        }
    }

    public double getSimilarity(Event other) {

        double minSum = this.users.keySet().stream()
                .map(k -> Math.min(this.users.get(k).getGrade(), other.getUsers().get(k).getGrade()))
                .reduce((Double::sum))
                .orElse(0.0);

        double sumWeight = Math.sqrt(this.sum) * Math.sqrt(other.getSum());

        return minSum / sumWeight;
    }
}
