package vn.thinhliendev.sporthub.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.thinhliendev.sporthub.admin.dto.AdminProductListItem;
import vn.thinhliendev.sporthub.admin.dto.ProductCreateForm;
import vn.thinhliendev.sporthub.admin.dto.StockFilter;
import vn.thinhliendev.sporthub.catalog.entity.Category;
import vn.thinhliendev.sporthub.catalog.entity.Product;
import vn.thinhliendev.sporthub.catalog.repository.CategoryRepository;
import vn.thinhliendev.sporthub.catalog.repository.ProductRepository;
import vn.thinhliendev.sporthub.inventory.entity.Inventory;
import vn.thinhliendev.sporthub.inventory.repository.InventoryRepository;

import java.util.Locale;

@Service
public class AdminProductService {

    private static final int PAGE_SIZE = 10;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public AdminProductService(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminProductListItem> findProducts(String keyword, StockFilter stockFilter, int page) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        int safePage = Math.max(page, 0);
        return productRepository.findForAdmin(normalizedKeyword, stockFilter.name(),
                PageRequest.of(safePage, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "updatedAt")));
    }

    @Transactional
    public Product createProduct(ProductCreateForm form) {
        String normalizedSku = form.getSku().trim().toUpperCase(Locale.ROOT);
        String normalizedSlug = form.getSlug().trim().toLowerCase(Locale.ROOT);
        if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
            throw new ProductAlreadyExistsException("sku", "This SKU is already in use");
        }
        if (productRepository.existsBySlugIgnoreCase(normalizedSlug)) {
            throw new ProductAlreadyExistsException("slug", "This slug is already in use");
        }

        Category category = categoryRepository.findById(form.getCategoryId())
                .filter(Category::isActive)
                .orElseThrow(() -> new IllegalArgumentException("The selected category is unavailable"));

        Product product = productRepository.save(new Product(
                category,
                form.getName().trim(),
                normalizedSlug,
                normalizedSku,
                trimToNull(form.getDescription()),
                form.getPrice(),
                trimToNull(form.getImageUrl())));
        inventoryRepository.save(new Inventory(product, form.getQuantity()));
        return product;
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
