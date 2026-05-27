package main.java.ru.practicum.model;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@ToString
public class Event {

    private final int id;
    private final Map<Integer, User> users;
    private volatile double sum;
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

            sum -= owner.getGrade();
            owner.set(user.getGrade(), user.getInstant());
            sum += owner.getGrade();
        } else {
            users.put(user.getId(), user);
            sum += user.getGrade();
        }

        log.info(Message.SUM_WEIGHT, sum);
    }

    public double getSimilarity(Event other) {

        double minSum = this.users.keySet().stream()
                .filter(k -> other.getUsers().containsKey(k))
                .map(k -> Math.min(this.users.get(k).getGrade(), other.getUsers().get(k).getGrade()))
                .peek(g -> log.info(Message.SUM_RESULT, g, this.id, other.getId()))
                .reduce((Double::sum))
                .orElse(0.0);

        log.info(Message.MIN_ACCORDING, minSum);

        double sumWeight = Math.sqrt(this.sum) * Math.sqrt(other.getSum());

        log.info(Message.SUM_WEIGHT, sumWeight);

        return minSum / sumWeight;
    }
}
