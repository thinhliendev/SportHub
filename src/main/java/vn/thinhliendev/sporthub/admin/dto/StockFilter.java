package vn.thinhliendev.sporthub.admin.dto;

public enum StockFilter {
    ALL("All stock levels"),
    IN_STOCK("In stock"),
    LOW_STOCK("Low stock (1-5)"),
    OUT_OF_STOCK("Out of stock");

    private final String label;

    StockFilter(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StockFilter from(String value) {
        if (value == null || value.isBlank()) {
            return ALL;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return ALL;
        }
    }
}
