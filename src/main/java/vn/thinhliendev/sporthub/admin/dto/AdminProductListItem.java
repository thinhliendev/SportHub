package vn.thinhliendev.sporthub.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminProductListItem(
        Long id,
        String name,
        String slug,
        String sku,
        String categoryName,
        BigDecimal price,
        int quantity,
        boolean active,
        LocalDateTime updatedAt
) {
    public boolean outOfStock() {
        return quantity == 0;
    }

    public boolean lowStock() {
        return quantity > 0 && quantity <= 5;
    }
}
