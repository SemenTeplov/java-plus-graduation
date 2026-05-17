package main.java.ru.practicum.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.mapper.UserActionMapper;
import main.java.ru.practicum.persistence.model.UserAction;
import main.java.ru.practicum.persistence.repository.UserActionRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionListener {

    private final UserActionRepository userActionRepository;

    private final UserActionMapper userActionMapper;

    @KafkaListener(topics = "${aggregator.kafka.topics.user}", containerFactory = Values.USER_CONSUMER)
    public void handler(UserActionAvro action, Acknowledgment acknowledgment) {

        log.info(Message.GET_USER_ACTION, action);

        UserAction userAction = userActionMapper.toUserAction(action);
        Optional<UserAction> opExistUser = userActionRepository.findById(userAction.getUserActionId());

        if (opExistUser.isEmpty() || !opExistUser.get().equals(userAction)) {

            userActionRepository.save(userAction);
        }

        acknowledgment.acknowledge();
    }
}
