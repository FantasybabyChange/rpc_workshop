package com.fantasybaby.examples.stream;

import com.kuka.rcs.bd.gprc.AutoChargingConfigResponse;
import com.kuka.rcs.bd.gprc.AutoChargingConfigServiceGrpc;
import com.kuka.rcs.bd.grpc.AutoChargingConfigGto;
import com.kuka.rcs.bd.grpc.EmptyParameterRequest;
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
public class GreeterAutoChargingRequestClient {
    Channel channel = ManagedChannelBuilder
            .forAddress("127.0.0.1", 7999)
            .usePlaintext()
            .build();
    AutoChargingConfigServiceGrpc.AutoChargingConfigServiceBlockingStub missionServiceStub = AutoChargingConfigServiceGrpc.newBlockingStub(channel);
    AutoChargingConfigServiceGrpc.AutoChargingConfigServiceStub autoChargingConfigServiceStub = AutoChargingConfigServiceGrpc.newStub(channel);

    public void getAllAutoChargingConfig() {
        AutoChargingConfigResponse allAutoChargingConfig = missionServiceStub.getAllAutoChargingConfig(EmptyParameterRequest.newBuilder().build());
        System.out.println(allAutoChargingConfig);
    }

    public void subsribeAutoCharging() {
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<AutoChargingConfigResponse> responseObserver = new StreamObserver<AutoChargingConfigResponse>() {
            @Override
            public void onNext(AutoChargingConfigResponse missionResponse) {
                System.out.println("返回了结果\n");
                AutoChargingConfigGto autoChargingConfig = missionResponse.getAutoChargingConfig();
                log.info("mission:{}", autoChargingConfig);

            }

            @Override
            public void onError(Throwable throwable) {
                log.error("error:{}", throwable);
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subsribeAutoCharging();
            }

            @Override
            public void onCompleted() {
                log.error("on completed");
                latch.countDown();
            }
        };

        autoChargingConfigServiceStub.subscribeAutoChargingConfig(EmptyParameterRequest.newBuilder().build(), responseObserver);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        FINISHED
        GreeterAutoChargingRequestClient greeterMissionStreamClient = new GreeterAutoChargingRequestClient();
//        greeterMissionStreamClient.requestMissionUpdate();
//        greeterMissionStreamClient.getAllAutoChargingConfig();
        greeterMissionStreamClient.subsribeAutoCharging();
    }
}
