package com.github.nrao.grpc.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc.GreetServiceImplBase;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // get request
        Greeting greeting = request.getGreeting();
        // Construct response.
        String responseString = "Hello "+ greeting.getFirstName() + "!";
        GreetResponse greetResponse = GreetResponse.newBuilder().setResult(responseString).build();
        // send response.
        responseObserver.onNext(greetResponse);
        // complete the rpc call
        responseObserver.onCompleted();
    }
}
