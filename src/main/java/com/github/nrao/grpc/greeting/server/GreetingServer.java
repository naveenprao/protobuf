package com.github.nrao.grpc.greeting.server;

import com.service.calculator.server.CalculatorServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) {
        System.out.println("hello grpc");
        Server server = ServerBuilder.forPort(50001)
                .addService(new GreetServiceImpl())
                .addService(new CalculatorServiceImpl())
                .build();
        try {
            server.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("received shutdown requrest");
                server.shutdown();
                System.out.println("successfully stopped the server");
            }));
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
