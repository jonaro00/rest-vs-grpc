package inventory.api.item;

/**
 * Represents a summary of items for a specific item type
 */
public class ItemSummary {
    private ItemType itemType;
    private int count;

    /**
     * Constructor for ItemSummary object
     *
     * @param itemType The item type
     * @param count    The count of items
     */
    public ItemSummary(ItemType itemType, int count) {
        this.itemType = itemType;
        this.count = count;
    }

    /* Getters and setters */
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
