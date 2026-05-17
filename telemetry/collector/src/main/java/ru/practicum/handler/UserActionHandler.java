package main.java.ru.practicum.handler;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import stats.messages.UserActionProto;

import java.time.Instant;

public interface UserActionHandler {

    void handle(UserActionProto action);

    default ProducerRecord<String, SpecificRecordBase> getRecord(
        UserActionProto action, String topic) {

            return new ProducerRecord<>(
                    topic, null,
                    UserActionAvro.newBuilder()
                            .setUserId(action.getUserId())
                            .setEventId(action.getEventId())
                            .setActionType(ActionTypeAvro.valueOf(action.getActionType().toString().split("_")[1]))
                            .setTimestamp(Instant.parse(action.getTimestamp().toString()))
                            .build());
    }
}
