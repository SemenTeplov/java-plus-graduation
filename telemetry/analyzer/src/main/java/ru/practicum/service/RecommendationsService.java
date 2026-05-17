package main.java.ru.practicum.service;

import stats.messages.InteractionsCountRequestProto;
import stats.messages.RecommendedEventProto;
import stats.messages.SimilarEventsRequestProto;
import stats.messages.UserPredictionsRequestProto;

import java.util.List;

public interface RecommendationsService {

    List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto proto);

    List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto proto);

    List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto proto);
}
