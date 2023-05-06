package org.example.client;

import org.example.Empty;
import org.example.InventoryGrpc;
import org.example.ItemsStatusResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class ItemsStatusClient {
    private static final Logger logger = Logger.getLogger(ItemsStatusClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
        InventoryGrpc.InventoryStub stub = InventoryGrpc.newStub(channel);

        final CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<ItemsStatusResponse> responseObserver = new StreamObserver<ItemsStatusResponse>() {
            @Override
            public void onNext(ItemsStatusResponse response) {
                logger.info("Received items status response: " + response);
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error occurred: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Completed items status call.");
                latch.countDown();
            }
        };

        // Call the itemsStatus endpoint with an empty request
        stub.itemsStatus(Empty.getDefaultInstance(), responseObserver);

        // Wait for the response to come back
        latch.await();

        // Shutdown the channel
        channel.shutdown();
    }


}



