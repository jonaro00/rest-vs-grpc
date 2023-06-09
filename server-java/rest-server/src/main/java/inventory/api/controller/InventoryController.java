package inventory.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import inventory.api.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@RestController
public class InventoryController {
    

    /**
     * The heart_beat endpoint. Returns an empty response.
     *
     * @return A ResponseEntity representing the success status
     */
    @GetMapping("/heart_beat")
    public ResponseEntity<Void> heartBeat() {
        return ResponseEntity.ok().build();
    }

    /**
     * Items Status endpoint
     *
     * @return An ItemsStatusResponse object
     */
    @GetMapping("/items_status")
    public ItemsStatusResponse getItemsStatus() {
        return new ItemsStatusResponse(
                "success",
                Arrays.asList(
                        "Invalid barcode/serial number entered.",
                        "Unable to update inventory at this time. Please try again later.",
                        "Not enough stock available to fulfill the request.",
                        "Unable to read inventory data. Please check data source and try again.",
                        "Incomplete inventory records detected. Please verify counts and resubmit.",
                        "Duplicate item entries detected. Please review inventory records.",
                        "Invalid input format. Please ensure the data is in the correct format and submit again.",
                        "Unable to retrieve inventory history. Please try again later."),
                0.95f,
                12345,
                670432.55f,
                56.07f);
    }

    /**
     * Items Summary endpoint
     *
     * @return An ItemsSummaryResponse object
     */
    @GetMapping("/items_summary")
    public ItemsSummaryResponse getItemsSummary() {
        ItemCitySummary itemCitySummary = new ItemCitySummary(
                "50f15f5b-78b5-45b4-9bf0-6d3691e606fe",
                new ItemSummary(ItemType.COMPUTER, 12));
        List<ItemCitySummary> itemCitySummaries = Collections.nCopies(480, itemCitySummary);
        return new ItemsSummaryResponse(itemCitySummaries);
    }

    /**
     * Items Full endpoint
     *
     * @return An ItemsFullResponse object containing the details of all items
     */
    @GetMapping("/items_full")
    public ItemsFullResponse getItemsFull() {
        ItemDetails item = new ItemDetails(
                "6b99bcc5-4db2-4f15-95e9-0eb4d7762eb9",
                ItemType.COMPUTER,
                "Ferris",
                "Crab Hammer",
                "SN1605984635",
                59.90,
                false,
                new Location(
                        "a6a94b16-9aae-432a-9ef3-b11ff4d49709",
                        "SE",
                        null,
                        "Stockholm",
                        "Electrum",
                        3,
                        301,
                        null));
        List<ItemDetails> allItems = Collections.nCopies(1395, item);
        return new ItemsFullResponse(allItems);
    }
}
