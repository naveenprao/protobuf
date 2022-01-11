package com.service.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) {
        System.out.println("grpc server running!");
        Server server = ServerBuilder.forPort(50001)
                .addService(new CalculatorServiceImpl())
                .build();
        try {
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("received shutdown request");
                server.shutdown();
                System.out.println("successfully stopped the server");
            }));
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
