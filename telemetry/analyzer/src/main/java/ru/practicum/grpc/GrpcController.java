package main.java.ru.practicum.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import lombok.RequiredArgsConstructor;

import main.java.ru.practicum.service.RecommendationsService;
import net.devh.boot.grpc.server.service.GrpcService;

import stats.messages.InteractionsCountRequestProto;
import stats.messages.RecommendedEventProto;
import stats.messages.SimilarEventsRequestProto;
import stats.messages.UserPredictionsRequestProto;

import stats.service.collector.RecommendationsControllerGrpc;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class GrpcController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationsService recommendationsService;

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {

        try {

            List<RecommendedEventProto> list = recommendationsService.getInteractionsCount(request);

            if (list == null || list.isEmpty()) {

                responseObserver.onNext(RecommendedEventProto.getDefaultInstance());
            } else {

                list.forEach(responseObserver::onNext);
            }

            responseObserver.onCompleted();
        } catch (Exception e) {

            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)));
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {

        try {

            List<RecommendedEventProto> list = recommendationsService.getSimilarEvents(request);

            if (list == null || list.isEmpty()) {

                responseObserver.onNext(RecommendedEventProto.getDefaultInstance());
            } else {

                list.forEach(responseObserver::onNext);
            }

            responseObserver.onCompleted();
        } catch (Exception e) {

            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)));
        }
    }

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {

        try {

            List<RecommendedEventProto> list = recommendationsService.getRecommendationsForUser(request);

            if (list == null || list.isEmpty()) {

                responseObserver.onNext(RecommendedEventProto.getDefaultInstance());
            } else {

                list.forEach(responseObserver::onNext);
            }

            responseObserver.onCompleted();
        } catch (Exception e) {

            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)));
        }
    }
}
