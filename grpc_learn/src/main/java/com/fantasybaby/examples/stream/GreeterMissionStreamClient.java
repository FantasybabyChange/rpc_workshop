package com.fantasybaby.examples.stream;

import com.kuka.rcs.bd.grpc.EmptyParameterRequest;
import com.kuka.rcs.bd.grpc.MissionServiceGrpc;
import com.kuka.rcs.bd.grpc.SubscribeMission;
import com.kuka.rcs.bd.grpc.SubscribeMissionResponse;
import com.kuka.rcs.bd.grpc.map.MapServiceGrpc;
import com.kuka.rcs.bd.grpc.map.SubscribeMapRequest;
import com.kuka.rcs.bd.grpc.map.SubscribeMapResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * Created on 10/3/2021.
 *
 * @author Reid Liu
 */
@Slf4j
public class GreeterMissionStreamClient {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        Channel channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 7999)
                .usePlaintext()
                .build();
        //异步存根
        MissionServiceGrpc.MissionServiceStub missionServiceStub = MissionServiceGrpc.newStub(channel);
        StreamObserver<SubscribeMissionResponse> responseObserver = new StreamObserver<SubscribeMissionResponse>() {
            @Override
            public void onNext(SubscribeMissionResponse missionResponse) {
                System.out.println("返回了结果\n");
                SubscribeMission mission = missionResponse.getMission();
                log.info("mission:{}",mission);

            }

            @Override
            public void onError(Throwable throwable) {
                Thread.currentThread().interrupt();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();

            }
        };

        missionServiceStub.subscribeMissionStatus(EmptyParameterRequest.newBuilder().build(),responseObserver );
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
