package main.java.ru.practicum.grpc;

import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import lombok.RequiredArgsConstructor;

import main.java.ru.practicum.handler.UserActionHandler;

import net.devh.boot.grpc.server.service.GrpcService;

import stats.messages.UserActionProto;
import stats.service.collector.UserActionControllerGrpc;

@GrpcService
@RequiredArgsConstructor
public class GrpcController extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionHandler handler;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {

        try {

            handler.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {

            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)));
        }
    }
}
