package main.java.ru.practicum.service;

import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.status.EventStatus;

import org.springframework.stereotype.Service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AggregatorService {

    private final Map<Integer, Map<Integer, Double>> events;

    private final Map<Integer, Map<Integer, Double>> minAccordance;

    private final Map<Integer, Double> sumWeights;

    public AggregatorService() {

        this.events = new ConcurrentHashMap<>();
        this.minAccordance = new ConcurrentHashMap<>();
        this.sumWeights = new ConcurrentHashMap<>();
    }

    public Optional<List<EventSimilarityAvro>> updateState(UserActionAvro user) {

        log.info(Message.GET_LIST_OF_USER, user);

        if (!events.containsKey(user.getEventId())) {

            log.info(Message.EVENT_DON_T_EXIST, user.getEventId());

            Map<Integer, Double> userMap = new ConcurrentHashMap<>();

            userMap.put(user.getUserId(), EventStatus.getValue(user.getActionType().name()));

            for (var entry : events.entrySet()) {

                log.info(Message.PREPARED_EVENT, entry);

                Map<Integer, Double> eventMap = new ConcurrentHashMap<>();

                Integer[] compEvents = compareEvents(entry.getKey(), user.getEventId());

                eventMap.put(compEvents[1], getMinAccordance(entry, user));
                minAccordance.put(compEvents[0], eventMap);
            }

            sumWeights.put(user.getEventId(), EventStatus.getValue(user.getActionType().name()));
            events.put(user.getEventId(), userMap);
        } else {

            if (!events.get(user.getEventId()).containsKey(user.getUserId())) {

                log.info(Message.USER_DON_T_EXIST, user.getUserId());

                setSumWeight(user);
                setMinAccordance(user);
            } else if (events.get(
                    user.getEventId()).get(user.getUserId()) > EventStatus.getValue(user.getActionType().name())) {

                log.info(Message.USER_REDUCE_GRADLE, user.getUserId());

                setMinAccordance(user);
            } else if (events.get(
                    user.getEventId()).get(user.getUserId()) < EventStatus.getValue(user.getActionType().name())) {

                log.info(Message.USER_INCREASE_GRADLE, user.getUserId());

                setSumWeight(user);
            } else {

                log.info(Message.USER_DON_T_CHANGE_GRADLE, user.getUserId());

                return Optional.empty();
            }
        }

        return Optional.of(minAccordance.entrySet().stream()
                .filter(entry -> entry.getKey().equals(user.getEventId())
                        || entry.getValue().containsKey(user.getEventId()))
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .map(e -> {

                        return EventSimilarityAvro.newBuilder()
                                .setEventA(entry.getKey())
                                .setEventB(e.getKey())
                                .setScore(e.getValue()
                                        / (Math.sqrt(sumWeights.get(entry.getKey()))
                                        * Math.sqrt(sumWeights.get(e.getKey()))))
                                .setTimestamp(Instant.now())
                                .build();
                    }))
                .peek(e -> log.info(Message.TAKE_EVENTS_SIMILARITY, e.getEventA(), e.getEventB(), e.getScore()))
                .toList());
    }

    private Integer[] compareEvents(Integer eventA, Integer eventB) {

        log.info(Message.DEFINITION_ORDER, eventA, eventB);

        if (eventA < eventB) {

            return new Integer[] {eventA, eventB};
        } else {

            return new Integer[] {eventB, eventA};
        }
    }

    private double getMinAccordance(Map.Entry<Integer, Map<Integer, Double>> entry, UserActionAvro user) {

        log.info(Message.SUM, entry, user);

        double minSum = 0.0;

        for (var userId : entry.getValue().keySet()) {

            minSum += user.getUserId() == userId
                    ? Math.min(EventStatus.getValue(user.getActionType().name()), entry.getValue().get(userId))
                    : entry.getValue().get(userId);
        }

        log.info(Message.SUM_RESULT, minSum);

        return minSum;
    }

    private void setMinAccordance(UserActionAvro user) {

        log.info(Message.MIN_ACCORDING);

        for (var entry : minAccordance.entrySet()) {

            if (entry.getKey().equals(user.getEventId()) || entry.getValue().containsKey(user.getEventId())) {

                minAccordance.get(entry.getKey()).put(entry.getKey(), getMinAccordance(entry, user));
            }
        }
    }

    private void setSumWeight(UserActionAvro user) {

        log.info(Message.SUM_WEIGHT);

        Map<Integer, Double> userMap = new ConcurrentHashMap<>();

        userMap.put(user.getUserId(), EventStatus.getValue(user.getActionType().name()));

        events.put(user.getEventId(), userMap);
        sumWeights.put(user.getEventId(),
                sumWeights.get(user.getEventId()) + EventStatus.getValue(user.getActionType().name()));
    }
}
