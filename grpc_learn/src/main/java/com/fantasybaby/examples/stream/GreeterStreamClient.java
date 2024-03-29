package com.fantasybaby.examples.stream;

import com.kuka.rcs.bd.grpc.map.MapServiceGrpc;
import com.kuka.rcs.bd.grpc.map.SubscribeMapRequest;
import com.kuka.rcs.bd.grpc.map.SubscribeMapResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Created on 10/3/2021.
 *
 * @author Reid Liu
 */
public class GreeterStreamClient {
    public static void main(String[] args) {
        Channel channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 7999)
                .usePlaintext()
                .build();
        //异步存根
        MapServiceGrpc.MapServiceStub streamingGreeterStub = MapServiceGrpc.newStub(channel);
        StreamObserver<SubscribeMapResponse> responseObserver = new StreamObserver<SubscribeMapResponse>() {
            @Override
            public void onNext(SubscribeMapResponse helloReply) {
                System.out.println("返回了结果\n");

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
        SubscribeMapRequest request = SubscribeMapRequest.newBuilder().build();
        streamingGreeterStub.subscribeMap(request, responseObserver);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
