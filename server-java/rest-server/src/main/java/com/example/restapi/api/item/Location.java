package com.example.restapi.api.item;

public class Location {
    private String city_uuid;
    private String country;
    private String state;
    private String city;
    private String building;
    private Integer floor;
    private Integer room;
    private Integer cabinet_position;

    public String getCity_uuid() {
        return city_uuid;
    }

    public void setCity_uuid(String city_uuid) {
        this.city_uuid = city_uuid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getRoom() {
        return room;
    }

    public void setRoom(Integer room) {
        this.room = room;
    }

    public Integer getCabinet_position() {
        return cabinet_position;
    }

    public void setCabinet_position(Integer cabinet_position) {
        this.cabinet_position = cabinet_position;
    }
}
