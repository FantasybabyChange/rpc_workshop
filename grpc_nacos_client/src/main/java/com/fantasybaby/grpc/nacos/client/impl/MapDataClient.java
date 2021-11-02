package com.fantasybaby.grpc.nacos.client.impl;

import com.kuka.rcs.bd.grpc.*;
import com.kuka.rcs.bd.grpc.map.MapServiceGrpc;
import com.kuka.rcs.bd.grpc.map.NodeGto;
import com.kuka.rcs.bd.grpc.robot.RobotServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * Created on 11/2/2021.
 *
 * @author Reid Liu
 */
@Service
public class MapDataClient {

    @GrpcClient("rcs-basic-data")
    private MapServiceGrpc.MapServiceBlockingStub mapServiceBlockingStub;
    @GrpcClient("rcs-basic-data")
    private MapServiceGrpc.MapServiceStub mapServiceStub;


    public NodeGto getNode(Long id) {
        return mapServiceBlockingStub.getNodeById(OneIdRequest.newBuilder()
                .setId(id)
                .build())
                .getNode();
    }
}
