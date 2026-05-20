package main.java.ru.practicum.config;

import kafka.serializer.EventSimilarityDeserializer;
import kafka.serializer.UserActionDeserializer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${kafka.bootstrap-servers}")
    private String server;

    @Value("${kafka.offset.rest-config}")
    private String offsetRest;

    @Value("${kafka.consumer.session-timeout-ms}")
    private String sessionTimeout;

    @Value("${kafka.consumer.heartbeat-interval-ms}")
    private String heartbeatInterval;

    @Value("${kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Value("${kafka.consumer.group-id.analyzer}")
    private String analyzer;

    @Value("${kafka.consumer.group-id.aggregator}")
    private String aggregator;

    @Value("${kafka.consumer.interval-ms}")
    private String interval;

    @Bean
    public ConsumerFactory<String, UserActionAvro> userConsumerFactory() {

        Map<String, Object> configs = consumerConfig();

        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, aggregator);

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConsumerFactory<String, EventSimilarityAvro> eventsConsumerFactory() {

        Map<String, Object> configs = consumerConfig();

        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, analyzer);

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserActionAvro> userConsumer(
            ConsumerFactory<String, UserActionAvro> userConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, UserActionAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(userConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventSimilarityAvro> eventsConsumer(
            ConsumerFactory<String, EventSimilarityAvro> eventsConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, EventSimilarityAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(eventsConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    private Map<String, Object> consumerConfig() {

        Map<String, Object> configs = new HashMap<>();

        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        configs.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval);
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        configs.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, interval);

        return configs;
    }
}
