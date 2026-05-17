package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Message;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationsServiceImpl implements RecommendationsService {

    private final UserActionRepository userActionRepository;

    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto proto) {

        log.info(Message.GET_INTERACTIONS_COUNT, proto);

        return userActionRepository.getUsersByEventId(proto.getEventId()).stream()
                .map(u -> RecommendedEventProto.newBuilder()
                        .setEventId(Math.toIntExact(u.getUserActionId().getEventId()))
                        .setScore(ActionType.getValue(u.getActionType().name()))
                        .build())
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto proto) {

        log.info(Message.GET_SIMILAR_EVENTS, proto);

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
                .map(e -> RecommendedEventProto.newBuilder()
                        .setEventId(Math.toIntExact(e.getEventSimilarityId().getEventAId()))
                        .setScore(e.getScore())
                        .build())
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto proto) {

        log.info(Message.GET_RECOMMENDATIONS, proto);

        List<UserAction> eventsForUser = userActionRepository.getUsersById(proto.getUserId()).stream()
                .sorted(Comparator.comparing(UserAction::getTimestampMs))
                .toList();

        if (eventsForUser.isEmpty()) {

            return List.of();
        }

        List<UserAction> eventsWithoutUser = userActionRepository.getUsersWithoutId(proto.getUserId()).stream()
                .sorted(Comparator.comparing(UserAction::getTimestampMs))
                .toList();

        List<EventSimilarity> events = eventSimilarityRepository.getEventsByEventIds(eventsForUser.stream()
                .map(e -> e.getUserActionId().getEventId())
                .toArray(Long[]::new));

        double sumWeight = 0;

        for (var u : eventsForUser) {

            sumWeight += ActionType.getValue(u.getActionType().name()) * events.stream()
                    .filter(e ->
                            u.getUserActionId().getEventId().equals(e.getEventSimilarityId().getEventAId())
                            || u.getUserActionId().getEventId().equals(e.getEventSimilarityId().getEventBId()))
                    .map(EventSimilarity::getScore)
                    .findFirst()
                    .orElse(0.0);
        }

        double coefficient = eventsForUser.stream()
                .map(e -> ActionType.getValue(e.getActionType().name()))
                .reduce(Double::sum)
                .orElse(0.0);

        double newScore = sumWeight / coefficient;

        return eventsWithoutUser.stream().filter(e -> {

                    double score = ActionType.getValue(e.getActionType().name());

                    return Math.abs(score - newScore) >= 0.2 || Math.abs(score - newScore) <= 0.2;
                })
                .sorted((u1, u2) ->
                        (int) (ActionType.getValue(u1.getActionType().name()) -
                        ActionType.getValue(u2.getActionType().name())))
                .map(e -> RecommendedEventProto.newBuilder()
                        .setEventId(Math.toIntExact(e.getUserActionId().getEventId()))
                        .setScore(ActionType.getValue(e.getActionType().name()))
                        .build())
                .toList();
    }
}
