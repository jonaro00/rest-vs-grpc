package com.example.restapi.api.controller;

import com.example.restapi.api.item.ItemsStatusResponse;
import com.example.restapi.api.model.Message;
import com.example.restapi.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.example.restapi.api.item.ItemType;
import com.example.restapi.api.item.ItemsFullResponse;
import com.example.restapi.api.item.ItemCitySummary;
import com.example.restapi.api.item.ItemDetails;
import com.example.restapi.api.item.ItemSummary;
import com.example.restapi.api.item.ItemsSummaryResponse;
import com.example.restapi.api.item.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

@RestController
public class MessageController {

    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    // curl http://localhost:8080/message?id=1

    @GetMapping("/message")
    public Message getMessage(@RequestParam Integer id) {
        Optional message = messageService.getMessage(id);
        if (message.isPresent()) {
            return (Message) message.get();
        }
        return null;
    }

    @GetMapping("/heart_beat")
    public ResponseEntity<String> heartBeat() {
        return ResponseEntity.ok().body("");
    }

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
                "Unable to retrieve inventory history. Please try again later."
            ),
            0.95f,
            12345,
            670432.55f,
            56.07f
        );
    }

    @GetMapping("/items_summary")
    public ItemsSummaryResponse getItemsSummary() {
        List<ItemCitySummary> itemCitySummaries = new ArrayList<>();
        for (int i = 0; i < 480; i++) {
            ItemCitySummary itemCitySummary = new ItemCitySummary(
                    "50f15f5b-78b5-45b4-9bf0-6d3691e606fe",
                    new ItemSummary(ItemType.COMPUTER, 12)
            );
            itemCitySummaries.add(itemCitySummary);
        }
        return new ItemsSummaryResponse(itemCitySummaries);
    }

    @GetMapping("/items_full")
    public ItemsFullResponse getItemsFull() {
        List<ItemDetails> allItems = new ArrayList<>();
    
        // Create a single ItemDetails object with the desired details
        ItemDetails item = new ItemDetails();
        item.setUuid("6b99bcc5-4db2-4f15-95e9-0eb4d7762eb9");
        item.setItemType(ItemType.COMPUTER);
        item.setBrand("Ferris");
        item.setModel("Crab Hammer");
        item.setSerialNumber("SN1605984635");
        item.setPurchasePrice(59.90);
        item.setDiscarded(false);
        Location location = new Location();
        location.setCity_uuid("a6a94b16-9aae-432a-9ef3-b11ff4d49709");
        location.setCountry("SE");
        location.setCity("Stockholm");
        location.setBuilding("Electrum");
        location.setFloor(3);
        location.setRoom(301);
        item.setLocation(location);
    
        // Add 1395 copies of the ItemDetails object to the allItems list
        // O(n)
        for (int i = 0; i < 1395; i++) {
            allItems.add(item);
        }
    
        // Create a new ItemsFullResponse object with the allItems list and return it
        return new ItemsFullResponse(allItems);
    }
    

    
}
