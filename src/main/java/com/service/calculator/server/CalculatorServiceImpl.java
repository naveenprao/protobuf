package com.service.calculator.server;

import com.service.calculator.AddRequest;
import com.service.calculator.AddResponse;
import com.service.calculator.AverageRequest;
import com.service.calculator.AverageResponse;
import com.service.calculator.CalculatorServiceGrpc;
import com.service.calculator.MaxRequest;
import com.service.calculator.MaxResponse;
import com.service.calculator.PrimeFactorRequest;
import com.service.calculator.PrimeFactorResponse;
import io.grpc.stub.StreamObserver;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void add(AddRequest request, StreamObserver<AddResponse> responseObserver) {
        long sum = request.getFirst();
        for (int rest : request.getRestList()) {
            sum += rest;
        }
        AddResponse response = AddResponse.newBuilder().setResult(sum).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void primeFactor(PrimeFactorRequest request, StreamObserver<PrimeFactorResponse> responseObserver) {
        BigInteger primeFinder;
        long primeFactor = 2;
        long number = request.getNumber();
        while (number > 1) {
            if (number % primeFactor == 0) {
                responseObserver
                        .onNext(PrimeFactorResponse.newBuilder()
                                .setFactor(primeFactor)
                                .build());
                number = number / primeFactor;
            } else {
                primeFinder = new BigInteger(String.valueOf(primeFactor));
                primeFactor = Long.parseLong(primeFinder.nextProbablePrime().toString());
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseObserver) {

        StreamObserver<AverageRequest> requestStreamObserver = new StreamObserver<>() {
            private long itemCount = 0;
            private long sum = 0;

            @Override
            public void onNext(AverageRequest value) {
                // on receiving the next client-streamed message.
                itemCount++;
                sum += value.getNumber();
            }

            @Override
            public void onError(Throwable t) {
                // neglect client stream error.
            }

            @Override
            public void onCompleted() {
                System.out.println("Sum:" + sum);
                System.out.println("Items:" + itemCount);
                responseObserver.onNext(AverageResponse.newBuilder().setAverage(sum / itemCount).build());
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver) {
        StreamObserver<MaxRequest> requestStreamObserver = new StreamObserver<MaxRequest>() {
            Set<Integer> items = new HashSet<>();

            @Override
            public void onNext(MaxRequest value) {
                items.add(value.getValue());
                int max = Collections.max(items);
                responseObserver.onNext(MaxResponse.newBuilder().setValue(max).build());
            }

            @Override
            public void onError(Throwable t) {
                // do nothing.
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }
}
