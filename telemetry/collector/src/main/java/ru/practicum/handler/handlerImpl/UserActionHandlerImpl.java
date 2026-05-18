package main.java.ru.practicum.handler.handlerImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.handler.UserActionHandler;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import stats.messages.UserActionProto;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {

    private final Producer<String, SpecificRecordBase> actionProducer;

    @Value("kafka.topics.user")
    private String topic;

    @Override
    public void handle(UserActionProto action) {

        log.info(Message.SEND_ACTION, action);

        actionProducer.send(getRecord(action, topic));
    }
}
