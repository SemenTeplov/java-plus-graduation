package main.java.ru.practicum.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic eventConsumerTopic(@Value("${aggregator.kafka.topics.events}") String topic) {
        return TopicBuilder
                .name(topic)
                .build();
    }

    @Bean
    public NewTopic userConsumerTopic(@Value("${aggregator.kafka.topics.user}") String topic) {
        return TopicBuilder
                .name(topic)
                .build();
    }
}
