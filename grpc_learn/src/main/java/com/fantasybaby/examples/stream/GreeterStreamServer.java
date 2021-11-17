package com.fantasybaby.examples.stream;

import com.fantasybaby.examples.manualflowcontrol.ManualFlowControlServer;
import com.google.protobuf.StringValue;
import com.kuka.rcs.bd.grpc.map.MapCodeAndStatus;
import com.kuka.rcs.bd.grpc.map.MapServiceGrpc;
import com.kuka.rcs.bd.grpc.map.SubscribeMapRequest;
import com.kuka.rcs.bd.grpc.map.SubscribeMapResponse;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created on 10/3/2021.
 *
 * @author Reid Liu
 */
@Slf4j
public class GreeterStreamServer {

    private static final Logger logger =
            Logger.getLogger(ManualFlowControlServer.class.getName());

    public static void main(String[] args) throws InterruptedException, IOException {
        // Service class implementation
        MapServiceGrpc.MapServiceImplBase svc = new MapServiceGrpc.MapServiceImplBase() {
            public StreamObserver<SubscribeMapRequest> subscribeMap(StreamObserver<SubscribeMapResponse> responseObserver) {
                String requestUuid = UUID.randomUUID().toString();
                StreamObserver<SubscribeMapRequest> requestObserver = new StreamObserver<SubscribeMapRequest>() {
                    @Override
                    public void onNext(SubscribeMapRequest request) {
                        if (log.isDebugEnabled()) {
                            log.debug("SubscribeMapRequest: {}", request);
                        }
                        responseObserver.onNext(SubscribeMapResponse.newBuilder().setMapCodeAndStatus(MapCodeAndStatus.newBuilder().setMapCode(StringValue.newBuilder().setValue("123").build()).getDefaultInstanceForType()).build());
                    }

                    @Override
                    public void onError(Throwable throwable) {

                        log.error(throwable.getMessage(), throwable);
                        responseObserver.onError(throwable);
                    }

                    @Override
                    public void onCompleted() {
                        responseObserver.onCompleted();
                    }
                };
                return requestObserver;
            }

        };

        final Server server = ServerBuilder
                .forPort(50051)
                .addService(svc)
                .build()
                .start();

        logger.info("Listening on " + server.getPort());

        Runtime.getRuntime().

                addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                        System.err.println("Shutting down");
                        try {
                            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace(System.err);
                        }
                    }
                });
        server.awaitTermination();
    }

}
