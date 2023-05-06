package org.example.client;

import org.example.Empty;
import org.example.InventoryGrpc;
import org.example.ItemsSummaryResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class ItemsSummaryClient {
    private static final Logger logger = Logger.getLogger(ItemsSummaryClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        InventoryGrpc.InventoryStub stub = InventoryGrpc.newStub(channel);

        final CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<ItemsSummaryResponse> responseObserver = new StreamObserver<ItemsSummaryResponse>() {
            @Override
            public void onNext(ItemsSummaryResponse response) {
                logger.info("Received items status response: " + response);
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error occurred (items summary): " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Completed items summary call.");
                latch.countDown();
            }
        };

        // Call the itemsSummary endpoint with an empty request
        stub.itemsSummary(Empty.getDefaultInstance(), responseObserver);

        // Wait for the response to come back
        latch.await();

        // Shutdown the channel
        channel.shutdown();
    }
}
