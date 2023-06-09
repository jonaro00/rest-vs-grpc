package inventory.api.item;

/**
 * ItemType class
 */
public enum ItemType {
    UNSPECIFIED("UNSPECIFIED"),
    CHAIR("CHAIR"),
    TABLE("TABLE"),
    COMPUTER("COMPUTER"),
    MONITOR("MONITOR"),
    KEYBOARD("KEYBOARD"),
    MOUSE("MOUSE");

    private final String value;

    /**
     * Constructs a new ItemType enum constant with the given value
     *
     * @param value The string value of the item type
     */
    private ItemType(String value) {
        this.value = value;
    }

    /**
     * Returns the string value of the item type
     *
     * @return The string value of the item type
     */
    public String getValue() {
        return value;
    }
}
