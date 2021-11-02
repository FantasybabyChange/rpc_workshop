package com.fantasybaby.grpc.nacos.client;

import com.fantasybaby.grpc.nacos.client.impl.ContainerTypeClient;
import com.fantasybaby.grpc.nacos.client.impl.MapDataClient;
import com.kuka.rcs.bd.grpc.Container;
import com.kuka.rcs.bd.grpc.map.NodeGto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.Resource;

/**
 * Created on 11/2/2021.
 *
 * @author Reid Liu
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.fantasybaby.grpc.nacos"})
public class GrpcClientApplication implements CommandLineRunner {
    @Resource
    private ContainerTypeClient containerTypeClient;
    @Resource
    private MapDataClient mapDataClient;

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        NodeGto node = mapDataClient.getNode(1L);
        System.out.println(node);
    }
}
