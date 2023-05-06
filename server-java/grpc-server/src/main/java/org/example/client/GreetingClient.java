package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.GreetingGrpc;
import org.example.HelloRequest;
import org.example.HelloResponse;

public class GreetingClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        // a blocking stub means that the RPC call waits for the server to respond, and will either return a response or raise an exception.
        GreetingGrpc.GreetingBlockingStub stub = GreetingGrpc.newBlockingStub(channel);

        HelloRequest request = HelloRequest.newBuilder().setName("John").build();
        HelloResponse response = stub.greet(request);

        System.out.println("Text from GreetingClient: " + response.getGreeting());

        channel.shutdown();
    }
}
