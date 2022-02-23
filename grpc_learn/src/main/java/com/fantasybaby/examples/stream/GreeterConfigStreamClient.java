package com.fantasybaby.examples.stream;

import com.kuka.rcs.bd.gprc.ChangeStatus;
import com.kuka.rcs.bd.gprc.ObstacleDetectionServiceGrpc;
import com.kuka.rcs.bd.gprc.SubscribeObstacleConfigResponse;
import com.kuka.rcs.bd.gprc.SubscribeObstacleDetection;
import com.kuka.rcs.bd.grpc.EmptyParameterRequest;
import com.kuka.rcs.bd.grpc.ObstacleDetectionGto;
import com.kuka.rcs.bd.grpc.SubscribeMission;
import com.kuka.rcs.bd.grpc.SubscribeMissionResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created on 10/3/2021.
 *
 * @author Reid Liu
 */
@Slf4j
public class GreeterConfigStreamClient {
    CountDownLatch latch = new CountDownLatch(1);
    Channel channel = ManagedChannelBuilder
            .forAddress("127.0.0.1", 7999)
            .usePlaintext()
            .build();
    ObstacleDetectionServiceGrpc.ObstacleDetectionServiceStub obstacleServiceStub = ObstacleDetectionServiceGrpc.newStub(channel);

    public static void main(String[] args) {

        GreeterConfigStreamClient greeterMissionStreamClient = new GreeterConfigStreamClient();
        CountDownLatch countDownLatch = greeterMissionStreamClient.runTest();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public CountDownLatch runTest() {
        return subscribeMission();
    }

    public CountDownLatch subscribeMission() {
        StreamObserver<SubscribeObstacleConfigResponse> responseObserver = new StreamObserver<SubscribeObstacleConfigResponse>() {
            @Override
            public void onNext(SubscribeObstacleConfigResponse missionResponse) {
                System.out.println("返回了结果\n");
                SubscribeObstacleDetection subscribeObstacleDetection = missionResponse.getSubscribeObstacleDetection();
                ChangeStatus changeStatus = subscribeObstacleDetection.getChangeStatus();
                log.info("status:{}", changeStatus);
                ObstacleDetectionGto obstacleDetection = subscribeObstacleDetection.getObstacleDetection();
                log.info("ObstacleDetection :{}", obstacleDetection);

            }

            @Override
            public void onError(Throwable throwable) {
                log.error("error:{}", throwable);
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscribeMission();
            }

            @Override
            public void onCompleted() {
                log.error("on completed");
                latch.countDown();
            }
        };

        obstacleServiceStub.subscribeObstacleDetection(EmptyParameterRequest.newBuilder().build(), responseObserver);
        return latch;
    }


}
