package inventory.api.item;

import java.util.List;

/**
 * Represents a response containing a list of item details.
 */
public class ItemsFullResponse {
    private List<ItemDetails> allItems;

    /**
     * Constructs a new ItemsFullResponse object with the given list of item details
     *
     * @param allItems The list of item details
     */
    public ItemsFullResponse(List<ItemDetails> allItems) {
        this.allItems = allItems;
    }

    /**
     * Get the list of item details
     *
     * @return The list of item details
     */
    public List<ItemDetails> getAllItems() {
        return allItems;
    }

    /**
     * Set the list of item details
     *
     * @param allItems The list of item details
     */
    public void setAllItems(List<ItemDetails> allItems) {
        this.allItems = allItems;
    }
}
