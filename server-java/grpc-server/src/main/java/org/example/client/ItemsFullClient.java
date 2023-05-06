package org.example.client;

import org.example.Empty;
import org.example.InventoryGrpc;
import org.example.ItemDetails;
import org.example.ItemsFullResponse;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class ItemsFullClient {
    private static final Logger logger = Logger.getLogger(ItemsFullClient.class.getName());

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
        InventoryGrpc.InventoryStub stub = InventoryGrpc.newStub(channel);

        final CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<ItemsFullResponse> responseObserver = new StreamObserver<ItemsFullResponse>() {
            @Override
            public void onNext(ItemsFullResponse response) {
                List<ItemDetails> allItems = response.getAllItemsList();
                for (ItemDetails itemDetails : allItems) {
                    logger.info("Received item details: " + itemDetails);
                }
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error occurred: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Completed items full call.");
                latch.countDown();
            }
        };

        // Call the itemsFull endpoint with an empty request
        stub.itemsFull(Empty.getDefaultInstance(), responseObserver);

        // Wait for the response to come back
        latch.await();

        // Shutdown the channel
        channel.shutdown();
    }
}
