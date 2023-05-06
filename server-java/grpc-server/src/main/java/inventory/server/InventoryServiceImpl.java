package inventory.server;

import io.grpc.stub.StreamObserver;
import inventory.*;
import org.lognet.springboot.grpc.GRpcService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@GRpcService
public class InventoryServiceImpl extends InventoryGrpc.InventoryImplBase {

    @Override
    public void heartBeat(Empty request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void itemsStatus(Empty request, StreamObserver<ItemsStatusResponse> responseObserver) {
        // Implement the logic to generate the ItemsStatusResponse here
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
                        "Unable to retrieve inventory history. Please try again later."
                ))
                .setLoad(0.95f)
                .setTotalItemCount(12345)
                .setTotalPrice(670432.55f)
                .setAveragePrice(56.07f)
                .build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void itemsSummary(Empty request, StreamObserver<ItemsSummaryResponse> responseObserver) {
        List<ItemCitySummary> itemCitySummaries = new ArrayList<>();
        for (int i = 0; i < 480; i++) {
            String cityUuid = UUID.randomUUID().toString();
            ItemSummary itemSummary = ItemSummary.newBuilder()
                    .setItemType(ItemType.COMPUTER)
                    .setCount(12)
                    .build();
            ItemCitySummary itemCitySummary = ItemCitySummary.newBuilder()
                    .setCityUuid(cityUuid)
                    .setItemSummary(itemSummary)
                    .build();
            itemCitySummaries.add(itemCitySummary);
        }
        ItemsSummaryResponse response = ItemsSummaryResponse.newBuilder()
                .addAllItemCitySummaries(itemCitySummaries)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void itemsFull(Empty request, StreamObserver<ItemsFullResponse> responseObserver) {
        List<ItemDetails> allItems = new ArrayList<>();
        for (int i = 0; i < 1395; i++) {
            String uuid = UUID.randomUUID().toString();
            ItemDetails itemDetails = ItemDetails.newBuilder()
                    .setUuid(uuid)
                    .setItemType(ItemType.COMPUTER)
                    .setBrand("Ferris")
                    .setModel("Crab Hammer")
                    .setSerialNumber("SN1605984635")
                    .setPurchasePrice(59.90f)
                    .setDiscarded(false)
                    .setLocation(Location.newBuilder()
                            .setCityUuid(UUID.randomUUID().toString())
                            .setCountry("SE")
                            .setCity("Stockholm")
                            .setBuilding("Electrum")
                            .setFloor(3)
                            .setRoom(301))
                    .build();
            allItems.add(itemDetails);
        }
        ItemsFullResponse response = ItemsFullResponse.newBuilder()
                .addAllAllItems(allItems)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
