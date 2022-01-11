package com.service.calculator.client;

import com.google.common.collect.Lists;
import com.service.calculator.AddRequest;
import com.service.calculator.AddResponse;
import com.service.calculator.AverageRequest;
import com.service.calculator.AverageResponse;
import com.service.calculator.CalculatorServiceGrpc;
import com.service.calculator.CalculatorServiceGrpc.CalculatorServiceBlockingStub;
import com.service.calculator.MaxRequest;
import com.service.calculator.MaxResponse;
import com.service.calculator.PrimeFactorRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    private static ManagedChannel channel;

    @BeforeMethod
    private void setup() {
        channel = ManagedChannelBuilder
                .forAddress("localhost", 50001)
                .usePlaintext()
                .build();
    }

    @AfterMethod
    private void teardown() {
        channel.shutdown();
    }

    @Test
    // server streaming
    public void testPrimeFactorization() {
        CalculatorServiceBlockingStub calculatorServiceBlockingStub = CalculatorServiceGrpc.newBlockingStub(channel);
        calculatorServiceBlockingStub.primeFactor(PrimeFactorRequest.newBuilder().setNumber(567890).build())
                .forEachRemaining(response -> System.out.println(response.getFactor()));
    }

    @Test
    // unary request/response
    public void testAdd() {
        CalculatorServiceBlockingStub calculatorServiceClient = CalculatorServiceGrpc.newBlockingStub(channel);
        AddRequest addRequest = AddRequest.newBuilder().setFirst(100).addRest(99)
                .addAllRest(Lists.newArrayList(1, 2, 3)).build();
        AddResponse addResponse = calculatorServiceClient.add(addRequest);
        System.out.println(addResponse.getResult());
    }

    @Test
    // client streaming
    public void testAverage() throws InterruptedException {
        CalculatorServiceGrpc.CalculatorServiceStub calculatorServiceStub = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<AverageRequest> requestObserver = calculatorServiceStub.average(new StreamObserver<>() {
            @Override
            public void onNext(AverageResponse value) {
                // we get a response from the server.
                System.out.println("Got average from sever:" + value.getAverage());
            }

            @Override
            public void onError(Throwable t) {
                // we get an error from the server
            }

            @Override
            public void onCompleted() {
                // Server is done sending us data
                // On completed will be called right after onNext();
                System.out.println("Server done sending data!");
                latch.countDown();
            }
        });

        for (int i = 1; i < 100; i++) {
            requestObserver.onNext(AverageRequest.newBuilder().setNumber(i).build());
        }
        requestObserver.onCompleted();
        latch.await(3000, TimeUnit.MILLISECONDS);
        System.out.println("Done receiving response from Server!");
    }

    @Test
    // BiDi Streaming
    public void testMax() throws InterruptedException {
        CalculatorServiceGrpc.CalculatorServiceStub calculatorServiceStub = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<MaxRequest> requestObserver = calculatorServiceStub.max(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse value) {
                // we get a response from the server.
                System.out.println("Got max from sever:" + value.getValue());
            }

            @Override
            public void onError(Throwable t) {
                // we get an error from the server
            }

            @Override
            public void onCompleted() {
                // Server is done sending us data
                // On completed will be called right after onNext();
                System.out.println("Server done sending data!");
                latch.countDown();
            }
        });
        Random random = new Random();
        for (int i = 1; i < 100; i++) {
            int newNumber = random.nextInt(100);
            System.out.println("Sending new number:" + newNumber);
            requestObserver.onNext(MaxRequest.newBuilder().setValue(newNumber).build());
            Thread.sleep(200);
        }
        requestObserver.onCompleted();
        latch.await(3000, TimeUnit.MILLISECONDS);
        System.out.println("Done receiving response from Server!");
    }
}
