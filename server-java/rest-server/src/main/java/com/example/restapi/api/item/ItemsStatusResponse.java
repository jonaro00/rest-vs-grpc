package com.example.restapi.api.item;

import java.util.List;

public class ItemsStatusResponse {
    private String status;
    private List<String> errors;
    private float load;
    private int total_item_count;
    private float total_price;
    private float average_price;

    public ItemsStatusResponse(String status, List<String> errors, float load, int total_item_count, float total_price, float average_price) {
        this.status = status;
        this.errors = errors;
        this.load = load;
        this.total_item_count = total_item_count;
        this.total_price = total_price;
        this.average_price = average_price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public float getLoad() {
        return load;
    }

    public void setLoad(float load) {
        this.load = load;
    }

    public int getTotal_item_count() {
        return total_item_count;
    }

    public void setTotal_item_count(int total_item_count) {
        this.total_item_count = total_item_count;
    }

    public float getTotal_price() {
        return total_price;
    }

    public void setTotal_price(float total_price) {
        this.total_price = total_price;
    }

    public float getAverage_price() {
        return average_price;
    }

    public void setAverage_price(float average_price) {
        this.average_price = average_price;
    }
}
