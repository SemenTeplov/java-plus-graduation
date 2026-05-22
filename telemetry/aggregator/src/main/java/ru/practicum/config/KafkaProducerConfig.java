package main.java.ru.practicum.config;

import kafka.serializer.GeneralAvroSerializer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String server;

    @Value("${kafka.producer.acks-config}")
    private String acksConfig;

    @Value("${kafka.producer.retries-config}")
    private String retriesConfig;

    @Value("${kafka.producer.retry-backoff}")
    private String retryBackoff;

    @Value("${kafka.producer.enable-idempotence}")
    private String enableIdempotence;

    @Value("${kafka.producer.max-connection}")
    private String maxConnection;

    @Bean
    public ProducerFactory<String, EventSimilarityAvro> producerFactory() {

        Map<String, Object> configs = new HashMap<>();

        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);
        configs.put(ProducerConfig.ACKS_CONFIG, acksConfig);
        configs.put(ProducerConfig.RETRIES_CONFIG, retriesConfig);
        configs.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoff);
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
        configs.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxConnection);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate() {

        return new KafkaTemplate<>(producerFactory());
    }
}
