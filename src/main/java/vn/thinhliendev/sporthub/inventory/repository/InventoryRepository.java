package vn.thinhliendev.sporthub.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.thinhliendev.sporthub.inventory.entity.Inventory;

import java.util.Optional;
import java.util.Collection;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);
    List<Inventory> findByProductIdIn(Collection<Long> productIds);
}
