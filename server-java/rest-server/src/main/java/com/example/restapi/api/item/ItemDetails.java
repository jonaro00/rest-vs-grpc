package com.example.restapi.api.item;

public class ItemDetails {
    private String uuid;
    private ItemType itemType;
    private String brand;
    private String model;
    private String serialNumber;
    private Double purchasePrice;
    private Boolean discarded;
    private Location location;

    public ItemDetails() {}

    public ItemDetails(String uuid, ItemType itemType, String brand, String model,
            String serialNumber, Double purchasePrice, Boolean discarded, Location location) {
        this.uuid = uuid;
        this.itemType = itemType;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.purchasePrice = purchasePrice;
        this.discarded = discarded;
        this.location = location;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ItemType getitemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Boolean getDiscarded() {
        return discarded;
    }

    public void setDiscarded(Boolean discarded) {
        this.discarded = discarded;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
