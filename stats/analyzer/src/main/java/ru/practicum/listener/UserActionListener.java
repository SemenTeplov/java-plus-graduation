package main.java.ru.practicum.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.mapper.UserActionMapper;
import main.java.ru.practicum.persistence.model.UserAction;
import main.java.ru.practicum.persistence.model.UserActionId;
import main.java.ru.practicum.persistence.repository.UserActionRepository;
import main.java.ru.practicum.persistence.status.ActionType;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionListener {

    private final UserActionRepository userActionRepository;

    private final UserActionMapper userActionMapper;

    private final CopyOnWriteArrayList<UserActionAvro> actions = new CopyOnWriteArrayList<>();

    @Scheduled(fixedDelay = Values.FIXED_DELAY)
    public void saveAction() {

        List<UserActionAvro> forRemove = new ArrayList<>(actions);

        for (UserActionAvro action : forRemove) {

            log.info(Message.GET_USER_ACTION, action);

            UserAction userAction = userActionMapper.toUserAction(action);

            UserAction oldUserAction = userActionRepository.findById(new UserActionId(
                    (long) action.getUserId(), (long) action.getEventId())).orElse(null);

            if (oldUserAction == null || ActionType.getValue(oldUserAction.getActionType().name())
                    < ActionType.getValue(userAction.getActionType().name())) {

                userActionRepository.save(userAction);
            }
        }

        actions.removeAll(forRemove);
    }

    @KafkaListener(topics = "${kafka.topics.user}", containerFactory = Values.USER_CONSUMER)
    public void handler(UserActionAvro action, Acknowledgment acknowledgment) {

        actions.add(action);
        acknowledgment.acknowledge();
    }
}
