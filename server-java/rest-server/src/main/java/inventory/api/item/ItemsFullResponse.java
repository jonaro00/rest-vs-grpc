package inventory.api.item;

import java.util.List;

public class ItemsFullResponse {
    private List<ItemDetails> allItems;

    public ItemsFullResponse(List<ItemDetails> allItems) {
        this.allItems = allItems;
    }

    public List<ItemDetails> getAllItems() {
        return allItems;
    }

    public void setAllItems(List<ItemDetails> allItems) {
        this.allItems = allItems;
    }
}
