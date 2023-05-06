package com.example.restapi.api.item;

import java.util.List;

public class ItemsFullResponse {
    private List<ItemDetails> all_items;

    public ItemsFullResponse(List<ItemDetails> all_items) {
        this.all_items = all_items;
    }

    public List<ItemDetails> getAllItems() {
        return all_items;
    }

    public void setAllItems(List<ItemDetails> all_items) {
        this.all_items = all_items;
    }
}
