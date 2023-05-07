package inventory.api.item;

public class Location {
    private String cityUuid;
    private String country;
    private String state;
    private String city;
    private String building;
    private Integer floor;
    private Integer room;
    private Integer cabinetPosition;

    public Location(String cityUuid, String country, String state, String city,
            String building, Integer floor, Integer room, Integer cabinetPosition) {
        this.cityUuid = cityUuid;
        this.country = country;
        this.state = state;
        this.city = city;
        this.building = building;
        this.floor = floor;
        this.room = room;
        this.cabinetPosition = cabinetPosition;
    }

    public String getCityUuid() {
        return cityUuid;
    }

    public void setCityUuid(String cityUuid) {
        this.cityUuid = cityUuid;
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
        return cabinetPosition;
    }

    public void setCabinetPosition(Integer cabinetPosition) {
        this.cabinetPosition = cabinetPosition;
    }
}
