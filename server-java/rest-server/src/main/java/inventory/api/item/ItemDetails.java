package inventory.api.item;

/**
 * Represents the details of an item.
 */
public class ItemDetails {
    private String uuid;
    private ItemType itemType;
    private String brand;
    private String model;
    private String serialNumber;
    private Double purchasePrice;
    private Boolean discarded;
    private Location location;

    /**
     * Constructor for ItemDetails object with the given details
     *
     * @param uuid           The UUID of the item
     * @param itemType       The type of the item
     * @param brand          The brand of the item
     * @param model          The model of the item
     * @param serialNumber   The serial number of the item
     * @param purchasePrice  The purchase price of the item
     * @param discarded      Indicates whether the item is discarded
     * @param location       The location of the item
     */
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

    /* Getters and setters */
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
