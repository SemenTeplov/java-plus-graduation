package main.java.ru.practicum.service;

import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.model.Event;
import main.java.ru.practicum.model.User;

import org.springframework.stereotype.Service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AggregatorService {

    private final Map<Integer, Event> events;

    private final Map<Integer, Map<Integer, Double>> oldSimilarities;

    public AggregatorService() {

        this.events = new ConcurrentHashMap<>();
        this.oldSimilarities = new ConcurrentHashMap<>();
    }

    public Optional<Set<EventSimilarityAvro>> updateState(UserActionAvro user) {

        log.info(Message.GET_LIST_OF_USER, user);

        if (!events.containsKey(user.getEventId())) {

            log.info(Message.EVENT_DON_T_EXIST, user.getEventId());

            events.put(user.getEventId(), new Event(user.getEventId(),
                    new User(user.getUserId(), user.getActionType().name(), user.getTimestamp())));
        } else {

            events.get(user.getEventId())
                    .add(new User(user.getUserId(), user.getActionType().name(), user.getTimestamp()));
        }

        Set<EventSimilarityAvro> similarities = new HashSet<>();

        for (Event e : events.values()) {

            if (e.getId() != user.getEventId()
                    && e.getUsers().containsKey(user.getUserId())) {

                double similarity = e.getSimilarity(events.get(user.getEventId()));
                Integer[] eventIds = compareEvents(e.getId(), user.getEventId());

                if (!(oldSimilarities.containsKey(eventIds[0]) &&
                        oldSimilarities.get(eventIds[0]).containsKey(eventIds[1]))) {

                    similarities.add(EventSimilarityAvro.newBuilder()
                            .setEventA(eventIds[0])
                            .setEventB(eventIds[1])
                            .setScore(similarity)
                            .setTimestamp(Instant.now())
                            .build());

                    Map<Integer, Double> secondEvent = new ConcurrentHashMap<>();

                    if (!oldSimilarities.containsKey(eventIds[0])) {

                        secondEvent.put(eventIds[1], similarity);
                        oldSimilarities.put(eventIds[0], secondEvent);
                    } else {

                        oldSimilarities.get(eventIds[0]).put(eventIds[1], similarity);
                    }

                    log.info(Message.TAKE_EVENTS_SIMILARITY, eventIds[0], eventIds[1], similarity);
                }
            }
        }

        return Optional.of(similarities);
    }

    private Integer[] compareEvents(Integer eventA, Integer eventB) {

        log.info(Message.DEFINITION_ORDER, eventA, eventB);

        if (eventA < eventB) {

            return new Integer[] {eventA, eventB};
        } else {

            return new Integer[] {eventB, eventA};
        }
    }
}
