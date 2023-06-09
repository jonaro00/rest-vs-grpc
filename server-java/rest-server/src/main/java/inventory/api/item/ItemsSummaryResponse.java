package inventory.api.item;

import java.util.List;

/**
 * Represents a response containing the summaries of items for cities
 */
public class ItemsSummaryResponse {
    private List<ItemCitySummary> itemCitySummaries;

    /**
     * Constructs a new ItemsSummaryResponse object with the given item city summaries
     *
     * @param itemCitySummaries The list of item city summaries
     */
    public ItemsSummaryResponse(List<ItemCitySummary> itemCitySummaries) {
        this.itemCitySummaries = itemCitySummaries;
    }

    /**
     * Returns a list of item city summaries
     *
     * @return The list of item city summaries
     */
    public List<ItemCitySummary> getItemCitySummaries() {
        return itemCitySummaries;
    }

    /**
     * Sets a list of item city summaries
     *
     * @param itemCitySummaries The list of item city summaries
     */
    public void setItemCitySummaries(List<ItemCitySummary> itemCitySummaries) {
        this.itemCitySummaries = itemCitySummaries;
    }
}
