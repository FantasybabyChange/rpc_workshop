package com.fantasybaby.examples.stream;

import com.kuka.rcs.bd.grpc.*;
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
public class GreeterMissionRequestClient {
    Channel channel = ManagedChannelBuilder
            .forAddress("127.0.0.1", 7999)
            .usePlaintext()
            .build();
    MissionServiceGrpc.MissionServiceBlockingStub missionServiceStub = MissionServiceGrpc.newBlockingStub(channel);

    public void requestMissionUpdate() {
        missionServiceStub.updateMissionWithSubmissions(MissionAndSubmissionRequest.newBuilder()
                .setMission(Mission.newBuilder().setId(136410).setCode("m1").setStatus("CANCELED"))
                .addSubmissions(Submission.newBuilder().setStatus("CANCELED").build())
                .build());
    }

    public static void main(String[] args) {
//        FINISHED
        GreeterMissionRequestClient greeterMissionStreamClient = new GreeterMissionRequestClient();
        greeterMissionStreamClient.requestMissionUpdate();
    }
}
