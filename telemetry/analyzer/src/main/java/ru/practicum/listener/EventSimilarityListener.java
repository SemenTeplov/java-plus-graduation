package main.java.ru.practicum.listener;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.mapper.EventSimilarityMapper;
//import main.java.ru.practicum.persistence.model.EventSimilarity;
import main.java.ru.practicum.persistence.repository.EventSimilarityRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSimilarityListener {

    private final EventSimilarityRepository eventSimilarityRepository;

    private final EventSimilarityMapper eventSimilarityMapper;

    @Transactional
    @KafkaListener(topics = "${kafka.topics.events}", containerFactory = Values.EVENT_CONSUMER)
    public void handler(Set<EventSimilarityAvro> events, Acknowledgment acknowledgment) {

        log.info(Message.GET_EVENTS_SIMILARITY, events);

//        EventSimilarity eventSimilarity = eventSimilarityMapper.toEventSimilarity(event);
//
//        log.info(Message.SAVE_EVENTS_SIMILARITY, events);
//
//        eventSimilarityRepository.save(eventSimilarity);

        events.forEach(e -> {

            log.info(Message.SAVE_EVENTS_SIMILARITY, events);

            eventSimilarityRepository.save(eventSimilarityMapper.toEventSimilarity(e));
        });

        acknowledgment.acknowledge();
    }
}
