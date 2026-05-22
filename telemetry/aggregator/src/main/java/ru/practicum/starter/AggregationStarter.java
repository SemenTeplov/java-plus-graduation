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
import org.springframework.stereotype.Component;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import static java.lang.Thread.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final AggregatorService service;

    private final KafkaTemplate<String, EventSimilarityAvro> template;

    @Value("${kafka.topics.events}")
    private String eventTopic;

    @KafkaListener(topics = "${kafka.topics.user}", containerFactory = Values.EVENT_CONSUMER)
    public void handler(UserActionAvro event, Acknowledgment acknowledgment) throws InterruptedException {

        log.info(Message.GET_USER_ACTION_FROM_KAFKA, Values.EVENT_CONSUMER, event);

        service.updateState(event).ifPresent(list -> {

            log.info(Message.SEND_LIST, list);

            list.forEach(userActionAvro -> template.send(eventTopic, userActionAvro));
        });

        acknowledgment.acknowledge();
    }
}
