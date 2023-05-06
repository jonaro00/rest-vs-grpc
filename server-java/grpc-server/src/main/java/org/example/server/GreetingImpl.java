package org.example.server;

import io.grpc.stub.StreamObserver;
import org.example.GreetingGrpc;
import org.example.HelloRequest;
import org.example.HelloResponse;
import org.lognet.springboot.grpc.GRpcService;

@GRpcService
public class GreetingImpl extends GreetingGrpc.GreetingImplBase {
    @Override
    public void greet(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        String greeting = "This text is from the GreetingImpl gRPC Server. Hello " + request.getName();
        HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted(); // Says that the server has completed sending messages, and the client terminates the gRPC call
    }
}
