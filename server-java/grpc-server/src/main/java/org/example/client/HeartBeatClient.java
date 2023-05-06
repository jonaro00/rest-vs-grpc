package org.example.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.example.Empty;
import org.example.InventoryGrpc;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class HeartBeatClient {
    private static final Logger logger = Logger.getLogger(HeartBeatClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
        InventoryGrpc.InventoryStub stub = InventoryGrpc.newStub(channel);

        final CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<Empty> responseObserver = new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty response) {
                logger.info("Received empty response.");
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error occurred (heart beat): " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Completed heartbeat call.");
                latch.countDown();
            }
        };

        // Call the heartbeat endpoint with an empty request
        stub.heartBeat(Empty.getDefaultInstance(), responseObserver);

        // Wait for the response to come back
        latch.await();

        // Shutdown the channel
        channel.shutdown();
    }
}
