package main.java.ru.practicum.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.service.AggregatorService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final AggregatorService service;

    private final KafkaTemplate<String, EventSimilarityAvro> template;

    private final CopyOnWriteArrayList<UserActionAvro> events = new CopyOnWriteArrayList<>();

    @Value("${kafka.topics.events}")
    private String eventTopic;

    @Scheduled(fixedDelay = Values.FIXED_DELAY)
    public void sendSnapshots() {

        List<UserActionAvro> forRemove = new ArrayList<>(events);

        for (UserActionAvro event : forRemove) {

            log.info(Message.GET_USER_ACTION_FROM_KAFKA, Values.EVENT_CONSUMER, event);

            service.updateState(event).ifPresent(list -> {

                log.info(Message.SEND_LIST, list);

                list.forEach(userActionAvro -> {
                    try {

                        template.send(eventTopic, userActionAvro).get(5, TimeUnit.SECONDS);
                    } catch (Exception e) {

                        throw new RuntimeException(e);
                    }
                });
            });

            events.removeAll(forRemove);
        }
    }

    @KafkaListener(topics = "${kafka.topics.user}", containerFactory = Values.EVENT_CONSUMER)
    public void handler(UserActionAvro event, Acknowledgment acknowledgment) {

        events.addIfAbsent(event);
        acknowledgment.acknowledge();
    }
}
