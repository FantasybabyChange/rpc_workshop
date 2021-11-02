package com.fantasybaby.grpc.nacos.client.impl;

import com.kuka.rcs.bd.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

/**
 * Created on 11/2/2021.
 *
 * @author Reid Liu
 */
@Service
public class ContainerTypeClient  {
    @GrpcClient("rcs-basic-data")
    private ContainerServiceGrpc.ContainerServiceStub containerServiceStub;
    @GrpcClient("rcs-basic-data")
    private ContainerServiceGrpc.ContainerServiceBlockingStub containerServiceBlockingStub;
    @GrpcClient("rcs-basic-data")
    private ContainerModelServiceGrpc.ContainerModelServiceBlockingStub containerModelServiceBlockingStub;

    public Container getByCode(String code) {
        OneContainerResponse response = containerServiceBlockingStub.getContainerByCode(CodeRequest.newBuilder()
                .setCode(code)
                .build());
        return response.getContainerCount() == 1 ? response.getContainer(0) : null;
    }
}
