package inventory.api.item;

import java.util.List;

/**
 * Represents a response containing the status of items
 */
public class ItemsStatusResponse {
    private String status;
    private List<String> errors;
    private float load;
    private int totalItemCount;
    private float totalPrice;
    private float averagePrice;

    /**
     * Constructor for ItemsStatusResponse object
     *
     * @param status         The status of the items
     * @param errors         The list of errors
     * @param load           The load value
     * @param totalItemCount The total item count
     * @param totalPrice     The total price
     * @param averagePrice   The average price
     */
    public ItemsStatusResponse(String status, List<String> errors, float load,
            int totalItemCount, float totalPrice, float averagePrice) {
        this.status = status;
        this.errors = errors;
        this.load = load;
        this.totalItemCount = totalItemCount;
        this.totalPrice = totalPrice;
        this.averagePrice = averagePrice;
    }

    /* Getters and setters */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public float getLoad() {
        return load;
    }

    public void setLoad(float load) {
        this.load = load;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public void setTotalItemCount(int totalItemCount) {
        this.totalItemCount = totalItemCount;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public float getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(float averagePrice) {
        this.averagePrice = averagePrice;
    }
}
