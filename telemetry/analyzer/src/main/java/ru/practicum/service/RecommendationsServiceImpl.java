package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
import main.java.ru.practicum.mapper.EventSimilarityMapper;
import main.java.ru.practicum.mapper.UserActionMapper;
import main.java.ru.practicum.persistence.model.EventSimilarity;
import main.java.ru.practicum.persistence.model.UserAction;
import main.java.ru.practicum.persistence.repository.EventSimilarityRepository;
import main.java.ru.practicum.persistence.repository.UserActionRepository;
import main.java.ru.practicum.persistence.status.ActionType;

import org.springframework.stereotype.Service;

import stats.messages.InteractionsCountRequestProto;
import stats.messages.RecommendedEventProto;
import stats.messages.SimilarEventsRequestProto;
import stats.messages.UserPredictionsRequestProto;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationsServiceImpl implements RecommendationsService {

    private final UserActionRepository userActionRepository;

    private final EventSimilarityRepository eventSimilarityRepository;

    private final UserActionMapper userActionMapper;

    private final EventSimilarityMapper eventSimilarityMapper;

    @Override
    public Set<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto proto) {

        log.info(Message.GET_INTERACTIONS_COUNT, String.format(Message.GET_INTERACTIONS_COUNT_VALUE, proto.getEventIdList()));

        if (proto.getEventIdList().isEmpty()) {

            return Set.of();
        }

        return userActionRepository.getUsersByEventId(proto.getEventIdList().toArray(Integer[]::new))
                .stream()
                .peek(u -> log.info(Message.TAKE_USER_ACTION, u))
                .collect(Collectors.toMap(u ->
                                u.getUserActionId().getEventId(),
                        u -> ActionType.getValue(u.getActionType().name()),
                                Double::sum)).entrySet().stream()
                .map(u -> RecommendedEventProto.newBuilder()
                        .setEventId(Math.toIntExact(u.getKey()))
                        .setScore(u.getValue()).build())
                .peek(u -> log.info(Message.TAKE_INTERACTION_COUNT,
                        String.format(Message.TAKE_INTERACTION_COUNT_VALUE, u.getEventId(), u.getScore())))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto proto) {

        log.info(Message.GET_SIMILAR_EVENTS, proto.getEventId(), proto.getUserId(), proto.getMaxResults());

        List<EventSimilarity> events = eventSimilarityRepository.getEventsByEventId(proto.getEventId());
        List<UserAction> users = userActionRepository.getUsersById(proto.getUserId());

        return events.stream()
                .filter(e ->
                    !(users.stream().anyMatch(u ->
                            u.getUserActionId().getEventId().equals(e.getEventSimilarityId().getEventAId())) &&
                    users.stream().anyMatch(u ->
                            u.getUserActionId().getEventId().equals(e.getEventSimilarityId().getEventBId()))))
                .sorted(Comparator.comparing(EventSimilarity::getScore))
                .limit(proto.getMaxResults())
                .peek(u -> log.info(Message.TAKE_EVENT_SIMILARITY, u))
                .map(eventSimilarityMapper::toRecommendedEventProto)
                .peek(u -> log.info(Message.TAKE_RECOMMENDATIONS, u.getEventId(), u.getScore()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto proto) {

        log.info(Message.GET_RECOMMENDATIONS, proto);

        List<UserAction> userActions = userActionRepository.getUsersById(proto.getUserId()).stream()
                .peek(a -> log.info(Message.GET_USER_ACTION, a))
                .toList();

        if (userActions.isEmpty()) {

            log.info(Message.LIST_EMPTY);

            return Set.of();
        }

        List<EventSimilarity> events = eventSimilarityRepository
                .getEventsByEventIds(userActions.stream()
                        .map(u -> u.getUserActionId().getEventId()).toArray(Long[]::new));

        if (events.isEmpty()) {

            log.info(Message.LIST_EMPTY);

            return Set.of();
        }

        return userActions.stream()
                .flatMap(u -> choiceEventSimilarity(events, u.getUserActionId().getEventId())
                                .stream().map(e ->
                                RecommendedEventProto.newBuilder()
                                    .setEventId(getEventId(e, u.getUserActionId().getEventId()))
                                    .setScore(
                                            sumMultiplyGradeAndSimilarity(userActions, events, u.getUserActionId().getEventId()) /
                                            sumSimilarity(events, u.getUserActionId().getEventId()))
                                    .build()))
                .sorted((u1, u2) -> (int) (u1.getScore() - u2.getScore()))
                .limit(proto.getMaxResults())
                .collect(Collectors.toSet());
    }

    private double sumMultiplyGradeAndSimilarity(List<UserAction> userActions,
                                                 List<EventSimilarity> events,
                                                 long event) {

        return choiceEventSimilarity(events, event).stream()
                .map(e -> e.getScore() * getGrade(e, userActions))
                .reduce(Double::sum).orElse(0.0);
    }

    private double sumSimilarity(List<EventSimilarity> events, Long event) {

        return choiceEventSimilarity(events, event).stream()
                .map(EventSimilarity::getScore)
                .reduce(Double::sum).orElse(0.0);
    }

    private List<EventSimilarity> choiceEventSimilarity(List<EventSimilarity> events, Long event) {

        return events.stream().filter(e ->
                    e.getEventSimilarityId().getEventAId().equals(event) ||
                    e.getEventSimilarityId().getEventBId().equals(event))
                .toList();
    }

    private double getGrade(EventSimilarity eventSimilarity, List<UserAction> userActions) {

        return ActionType.getValue(userActions.stream()
                .filter(u ->
                        u.getUserActionId().getEventId().equals(eventSimilarity.getEventSimilarityId().getEventAId()) ||
                        u.getUserActionId().getEventId().equals(eventSimilarity.getEventSimilarityId().getEventBId()))
                .findFirst()
                .get().getActionType().name());
    }

    private int getEventId(EventSimilarity eventSimilarity, long event) {

        return Math.toIntExact(eventSimilarity.getEventSimilarityId().getEventAId().equals(event)
                ? eventSimilarity.getEventSimilarityId().getEventAId()
                : eventSimilarity.getEventSimilarityId().getEventBId());
    }
}
