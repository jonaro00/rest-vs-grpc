package com.example.restapi.api.item;

public class ItemCitySummary {
    private String city_uuid;
    private ItemSummary item_summary;
    
    public ItemCitySummary() {
        
    }
    
    public ItemCitySummary(String city_uuid, ItemSummary item_summary) {
        this.city_uuid = city_uuid;
        this.item_summary = item_summary;
    }
    
    public String getCityUuid() {
        return city_uuid;
    }
    
    public void setCityUuid(String city_uuid) {
        this.city_uuid = city_uuid;
    }
    
    public ItemSummary getItemSummary() {
        return item_summary;
    }
    
    public void setItemSummary(ItemSummary item_summary) {
        this.item_summary = item_summary;
    }
}
