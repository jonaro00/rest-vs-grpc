package inventory.api.item;

public class ItemCitySummary {
    private String cityUuid;
    private ItemSummary itemSummary;

    public ItemCitySummary(String cityUuid, ItemSummary itemSummary) {
        this.cityUuid = cityUuid;
        this.itemSummary = itemSummary;
    }

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
