package inventory.api.item;

public enum ItemType {
    UNSPECIFIED("UNSPECIFIED"),
    CHAIR("CHAIR"),
    TABLE("TABLE"),
    COMPUTER("COMPUTER"),
    MONITOR("MONITOR"),
    KEYBOARD("KEYBOARD"),
    MOUSE("MOUSE");

    private final String value;

    private ItemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
