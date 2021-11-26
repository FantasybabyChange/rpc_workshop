package com.fantasybaby.examples.stream;

import com.kuka.rcs.bd.grpc.EmptyParameterRequest;
import com.kuka.rcs.bd.grpc.MissionServiceGrpc;
import com.kuka.rcs.bd.grpc.SubscribeMission;
import com.kuka.rcs.bd.grpc.SubscribeMissionResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created on 10/3/2021.
 *
 * @author Reid Liu
 */
@Slf4j
public class GreeterMissionStreamClient {
    CountDownLatch latch = new CountDownLatch(1);
    Channel channel = ManagedChannelBuilder
            .forAddress("127.0.0.1", 7999)
            .usePlaintext()
            .build();
    MissionServiceGrpc.MissionServiceStub missionServiceStub = MissionServiceGrpc.newStub(channel);

    public static void main(String[] args) {

        GreeterMissionStreamClient greeterMissionStreamClient = new GreeterMissionStreamClient();
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
        StreamObserver<SubscribeMissionResponse> responseObserver = new StreamObserver<SubscribeMissionResponse>() {
            @Override
            public void onNext(SubscribeMissionResponse missionResponse) {
                System.out.println("返回了结果\n");
                SubscribeMission mission = missionResponse.getMission();
                log.info("mission:{}", mission);

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

        missionServiceStub.subscribeMissionStatus(EmptyParameterRequest.newBuilder().build(), responseObserver);
        return latch;
    }


}
