package com.fantasybaby.examples.stream;

import com.kuka.rcs.bd.grpc.*;
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
public class GreeterContainerStreamClient {
    CountDownLatch latch = new CountDownLatch(1);
    Channel channel = ManagedChannelBuilder
            .forAddress("127.0.0.1", 7999)
            .usePlaintext()
            .build();
    ContainerServiceGrpc.ContainerServiceStub c = ContainerServiceGrpc.newStub(channel);
    ContainerServiceGrpc.ContainerServiceBlockingStub cs = ContainerServiceGrpc.newBlockingStub(channel);

    public static void main(String[] args) {

        GreeterContainerStreamClient greeterMissionStreamClient = new GreeterContainerStreamClient();
        greeterMissionStreamClient.getAllContainer();
//        CountDownLatch countDownLatch = greeterMissionStreamClient.runTest();
//
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    public void getAllContainer(){
        ContainerResponse allContainers = cs.getAllContainers(EmptyParameterRequest.newBuilder().build());
        System.out.println(allContainers);
    }

    public CountDownLatch runTest() {
        return subscribeMission();
    }

    public CountDownLatch subscribeMission() {
        StreamObserver<SubscribeContainerResponse> responseObserver = new StreamObserver<SubscribeContainerResponse>() {
            @Override
            public void onNext(SubscribeContainerResponse missionResponse) {
                System.out.println("返回了结果\n");
                ContainerGto subscribeObstacleDetection = missionResponse.getContainer();
                log.info("ObstacleDetection :{}", subscribeObstacleDetection);
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

        c.subscribeContainer(EmptyParameterRequest.newBuilder().build(), responseObserver);
        return latch;
    }


}
