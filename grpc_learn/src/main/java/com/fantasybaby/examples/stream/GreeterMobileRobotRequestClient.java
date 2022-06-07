package com.fantasybaby.examples.stream;

import com.google.protobuf.DoubleValue;
import com.google.protobuf.StringValue;
import com.kuka.rcs.bd.grpc.robot.MobileRobotRealtimeGto;
import com.kuka.rcs.bd.grpc.robot.RobotServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created on 10/3/2021.
 *
 * @author Reid Liu
 */
@Slf4j
public class GreeterMobileRobotRequestClient {
    Channel channel = ManagedChannelBuilder
            .forAddress("127.0.0.1", 7999)
            .usePlaintext()
            .build();
    RobotServiceGrpc.RobotServiceBlockingStub missionServiceStub = RobotServiceGrpc.newBlockingStub(channel);

    public void pushMobileRobotStatus() {
        MobileRobotRealtimeGto gto = MobileRobotRealtimeGto.newBuilder().setRobotId(StringValue.newBuilder().setValue("3011").build())
                .setBatteryLevel(DoubleValue.newBuilder().setValue(3.6).build()).build();
        missionServiceStub.pushRobotRealtimeStatus(gto);
    }


    public static void main(String[] args) {
//        FINISHED
        GreeterMobileRobotRequestClient greeterMissionStreamClient = new GreeterMobileRobotRequestClient();
        greeterMissionStreamClient.pushMobileRobotStatus();
    }
}
