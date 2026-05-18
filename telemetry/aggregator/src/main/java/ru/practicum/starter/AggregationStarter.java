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

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final AggregatorService service;

    private final CopyOnWriteArrayList<UserActionAvro> sensors = new CopyOnWriteArrayList<>();

    private final KafkaTemplate<String, EventSimilarityAvro> template;

    @Value("${kafka.topics.events}")
    private String eventTopic;

    @Scheduled(fixedDelay = Values.FIXED_DELAY)
    public void sendSnapshots() {

        log.info(Message.GET_LIST_OF_SENSORS, sensors);

        List<UserActionAvro> temp = new ArrayList<>(sensors);

        for (UserActionAvro sensor : temp) {

           service.updateState(sensor).ifPresent(list -> {

               log.info(Message.SEND_LIST, list);

               list.forEach(userActionAvro -> template.send(eventTopic, userActionAvro));
           });

            sensors.remove(sensor);
        }
    }

    @KafkaListener(topics = "${kafka.topics.user}", containerFactory = Values.EVENT_CONSUMER)
    public void handler(UserActionAvro event, Acknowledgment acknowledgment) {

        log.info(Message.GET_USER_ACTION_FROM_KAFKA, Values.EVENT_CONSUMER, event);

        sensors.addIfAbsent(event);
        acknowledgment.acknowledge();
    }
}
