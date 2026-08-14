package vn.thinhliendev.sporthub.catalog.dto;

import java.math.BigDecimal;

public record ProductListItem(
        Long id,
        String name,
        String slug,
        String sku,
        String categoryName,
        BigDecimal price,
        String imageUrl,
        int quantity
) {
    public boolean inStock() {
        return quantity > 0;
    }
}
