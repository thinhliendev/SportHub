package vn.thinhliendev.sporthub.catalog.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thinhliendev.sporthub.catalog.dto.ProductListItem;
import vn.thinhliendev.sporthub.catalog.entity.Product;
import vn.thinhliendev.sporthub.catalog.repository.ProductRepository;
import vn.thinhliendev.sporthub.inventory.entity.Inventory;
import vn.thinhliendev.sporthub.inventory.repository.InventoryRepository;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final int PAGE_SIZE = 9;

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductListItem> findActiveProducts(int page) {
        int safePage = Math.max(page, 0);
        Page<Product> products = productRepository.findByActiveTrue(
                PageRequest.of(safePage, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt")));

        Map<Long, Inventory> inventoryByProductId = inventoryRepository
                .findByProductIdIn(products.stream().map(Product::getId).toList())
                .stream()
                .collect(Collectors.toMap(inventory -> inventory.getProduct().getId(), Function.identity()));

        return products.map(product -> {
            Inventory inventory = inventoryByProductId.get(product.getId());
            int quantity = inventory == null ? 0 : inventory.getQuantity();
            return new ProductListItem(
                    product.getId(), product.getName(), product.getSlug(), product.getSku(),
                    product.getCategory().getName(), product.getPrice(), product.getImageUrl(), quantity);
        });
    }
}
