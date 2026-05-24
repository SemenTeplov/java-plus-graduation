package main.java.ru.practicum.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.mapper.EventSimilarityMapper;
import main.java.ru.practicum.persistence.model.EventSimilarity;
import main.java.ru.practicum.persistence.repository.EventSimilarityRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityListener {

    private final EventSimilarityRepository eventSimilarityRepository;

    private final EventSimilarityMapper eventSimilarityMapper;

    @KafkaListener(topics = "${kafka.topics.events}", containerFactory = Values.EVENT_CONSUMER)
    public void handler(EventSimilarityAvro event, Acknowledgment acknowledgment) {

        log.info(Message.GET_EVENTS_SIMILARITY, event);

        EventSimilarity eventSimilarity = eventSimilarityMapper.toEventSimilarity(event);
        eventSimilarityRepository.save(eventSimilarity);

        acknowledgment.acknowledge();
    }
}
