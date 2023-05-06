package com.example.restapi.api.item;

public class ItemSummary {
    private ItemType itemType;
    private int count;

    public ItemSummary() {
    }

    public ItemSummary(ItemType itemType, int count) {
        this.itemType = itemType;
        this.count = count;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
