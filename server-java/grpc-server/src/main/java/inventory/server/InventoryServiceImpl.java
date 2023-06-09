package inventory.server;

import io.grpc.stub.StreamObserver;
import inventory.*;
import org.lognet.springboot.grpc.GRpcService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@GRpcService
public class InventoryServiceImpl extends InventoryGrpc.InventoryImplBase {

    /**
     * Overrides the heartBeat method to handle requests to the 'heartBeat' endpoint
     *
     * @param request          The Empty request
     * @param responseObserver The StreamObserver for sending the response
     */
    @Override
    public void heartBeat(Empty request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
    
    /**
     * Overrides the itemsStatus method to handle requests to the 'itemsStatus' endpoint
     *
     * @param request          The Empty request
     * @param responseObserver The StreamObserver for sending the response
     */
    @Override
    public void itemsStatus(Empty request, StreamObserver<ItemsStatusResponse> responseObserver) {
        ItemsStatusResponse response = ItemsStatusResponse.newBuilder()
                .setStatus("success")
                .addAllErrors(Arrays.asList(
                        "Invalid barcode/serial number entered.",
                        "Unable to update inventory at this time. Please try again later.",
                        "Not enough stock available to fulfill the request.",
                        "Unable to read inventory data. Please check data source and try again.",
                        "Incomplete inventory records detected. Please verify counts and resubmit.",
                        "Duplicate item entries detected. Please review inventory records.",
                        "Invalid input format. Please ensure the data is in the correct format and submit again.",
                        "Unable to retrieve inventory history. Please try again later."))
                .setLoad(0.95f)
                .setTotalItemCount(12345)
                .setTotalPrice(670432.55f)
                .setAveragePrice(56.07f)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Overrides the itemsSummary method to handle requests to the 'itemsSummary' endpoint
     *
     * @param request          The Empty request
     * @param responseObserver The StreamObserver for sending the response
     */
    @Override
    public void itemsSummary(Empty request, StreamObserver<ItemsSummaryResponse> responseObserver) {
        ItemCitySummary itemCitySummary = ItemCitySummary.newBuilder()
                .setCityUuid("50f15f5b-78b5-45b4-9bf0-6d3691e606fe")
                .setItemSummary(ItemSummary.newBuilder()
                        .setItemType(ItemType.COMPUTER)
                        .setCount(12)
                        .build())
                .build();
        List<ItemCitySummary> itemCitySummaries = Collections.nCopies(480, itemCitySummary);
        ItemsSummaryResponse response = ItemsSummaryResponse.newBuilder()
                .addAllItemCitySummaries(itemCitySummaries)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Overrides the itemsFull method to handle requests to the 'itemsFull' endpoint
     *
     * @param request          The Empty request
     * @param responseObserver The StreamObserver for sending the response
     */
    @Override
    public void itemsFull(Empty request, StreamObserver<ItemsFullResponse> responseObserver) {
        ItemDetails itemDetails = ItemDetails.newBuilder()
                .setUuid("6b99bcc5-4db2-4f15-95e9-0eb4d7762eb9")
                .setItemType(ItemType.COMPUTER)
                .setBrand("Ferris")
                .setModel("Crab Hammer")
                .setSerialNumber("SN1605984635")
                .setPurchasePrice(59.90f)
                .setDiscarded(false)
                .setLocation(Location.newBuilder()
                        .setCityUuid("a6a94b16-9aae-432a-9ef3-b11ff4d49709")
                        .setCountry("SE")
                        .setCity("Stockholm")
                        .setBuilding("Electrum")
                        .setFloor(3)
                        .setRoom(301))
                .build();
        List<ItemDetails> allItems = Collections.nCopies(1395, itemDetails);
        ItemsFullResponse response = ItemsFullResponse.newBuilder()
                .addAllAllItems(allItems)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
