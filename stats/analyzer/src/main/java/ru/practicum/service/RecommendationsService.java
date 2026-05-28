package main.java.ru.practicum.service;

import stats.messages.InteractionsCountRequestProto;
import stats.messages.RecommendedEventProto;
import stats.messages.SimilarEventsRequestProto;
import stats.messages.UserPredictionsRequestProto;

import java.util.Set;

public interface RecommendationsService {

    Set<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto proto);

    Set<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto proto);

    Set<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto proto);
}
