package inventory.api.item;

/**
 * Location class
 */
public class Location {
    private String cityUuid;
    private String country;
    private String state;
    private String city;
    private String building;
    private Integer floor;
    private Integer room;
    private Integer cabinetPosition;

    /**
     * Constructs a new Location object with the given location details
     *
     * @param cityUuid         The UUID of the city
     * @param country          The country of the location
     * @param state            The state of the location
     * @param city             The city of the location
     * @param building         The building of the location
     * @param floor            The floor of the location
     * @param room             The room of the location
     * @param cabinetPosition  The cabinet position of the location
     */
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

    /* Getters and setters */
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
