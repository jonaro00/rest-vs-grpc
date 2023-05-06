package com.example.restapi.api.item;

import java.util.List;

public class ItemsSummaryResponse {
    private List<ItemCitySummary> itemCitySummaries;

    public ItemsSummaryResponse(List<ItemCitySummary> itemCitySummaries) {
        this.itemCitySummaries = itemCitySummaries;
    }
    

    public List<ItemCitySummary> getItemCitySummaries() {
        return itemCitySummaries;
    }

    public void setItemCitySummaries(List<ItemCitySummary> itemCitySummaries) {
        this.itemCitySummaries = itemCitySummaries;
    }
}

