package inventory.api.item;

/**
 * Represents a summary of items for a specific city
 */
public class ItemCitySummary {
    private String cityUuid;
    private ItemSummary itemSummary;

    /**
     * Constructs a new ItemCitySummary object with the given city UUID and item summary
     *
     * @param cityUuid     The UUID of the city
     * @param itemSummary  The summary of items for the city
     */
    public ItemCitySummary(String cityUuid, ItemSummary itemSummary) {
        this.cityUuid = cityUuid;
        this.itemSummary = itemSummary;
    }

    /* Getters and setters */
    public String getCityUuid() {
        return cityUuid;
    }

    public void setCityUuid(String cityUuid) {
        this.cityUuid = cityUuid;
    }

    public ItemSummary getItemSummary() {
        return itemSummary;
    }

    public void setItemSummary(ItemSummary itemSummary) {
        this.itemSummary = itemSummary;
    }
}
