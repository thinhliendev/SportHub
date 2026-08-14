package vn.thinhliendev.sporthub.inventory.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryTest {

    @Test
    void rejectsNegativeQuantity() {
        assertThatThrownBy(() -> new Inventory(null, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Inventory quantity cannot be negative");
    }
}
