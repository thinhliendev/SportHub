package vn.thinhliendev.sporthub.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.thinhliendev.sporthub.catalog.entity.Category;
import vn.thinhliendev.sporthub.catalog.entity.Product;
import vn.thinhliendev.sporthub.catalog.repository.CategoryRepository;
import vn.thinhliendev.sporthub.catalog.repository.ProductRepository;
import vn.thinhliendev.sporthub.inventory.entity.Inventory;
import vn.thinhliendev.sporthub.inventory.repository.InventoryRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Profile("dev")
public class CatalogDataInitializer implements ApplicationRunner {

    private static final String SAMPLE_DATA = "data/sample-products.csv";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public CatalogDataInitializer(CategoryRepository categoryRepository,
                                  ProductRepository productRepository,
                                  InventoryRepository inventoryRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        Map<String, Category> categories = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(category -> category.getName().toLowerCase(Locale.ROOT),
                        Function.identity(), (first, ignored) -> first, HashMap::new));
        List<Product> existingProducts = productRepository.findAll();
        Map<String, Product> productsBySku = existingProducts.stream()
                .collect(Collectors.toMap(product -> product.getSku().toLowerCase(Locale.ROOT),
                        Function.identity(), (first, ignored) -> first, HashMap::new));
        Map<String, Product> productsBySlug = existingProducts.stream()
                .collect(Collectors.toMap(product -> product.getSlug().toLowerCase(Locale.ROOT),
                        Function.identity(), (first, ignored) -> first, HashMap::new));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource(SAMPLE_DATA).getInputStream(), StandardCharsets.UTF_8))) {
            reader.lines().skip(1).filter(line -> !line.isBlank())
                    .forEach(line -> importRow(line, categories, productsBySku, productsBySlug));
        }
    }

    private void importRow(String line, Map<String, Category> categories,
                           Map<String, Product> productsBySku, Map<String, Product> productsBySlug) {
        String[] columns = line.split(",", -1);
        if (columns.length != 7) {
            throw new IllegalStateException("Invalid sample product row: " + line);
        }

        String categoryName = columns[0].trim();
        String name = columns[1].trim();
        String slug = columns[2].trim().toLowerCase(Locale.ROOT);
        String sku = columns[3].trim().toUpperCase(Locale.ROOT);
        BigDecimal price = new BigDecimal(columns[4].trim());
        int quantity = Integer.parseInt(columns[5].trim());
        String imageUrl = columns[6].trim();

        Category category = categories.computeIfAbsent(categoryName.toLowerCase(Locale.ROOT),
                ignored -> createCategory(categoryName));
        Product existingBySku = productsBySku.get(sku.toLowerCase(Locale.ROOT));
        if (existingBySku != null) {
            if (existingBySku.getImageUrl() == null || existingBySku.getImageUrl().startsWith("http")) {
                migrateLegacyProduct(existingBySku, category, name, slug, sku, price, quantity, imageUrl);
            }
            return;
        }

        Product existingBySlug = productsBySlug.get(slug);
        if (existingBySlug != null) {
            migrateLegacyProduct(existingBySlug, category, name, slug, sku, price, quantity, imageUrl);
            productsBySku.put(sku.toLowerCase(Locale.ROOT), existingBySlug);
            return;
        }

        Product product = createProduct(category, name, slug, sku, price, quantity, imageUrl);
        productsBySku.put(sku.toLowerCase(Locale.ROOT), product);
        productsBySlug.put(slug, product);
    }

    private Category createCategory(String name) {
        String slug = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return categoryRepository.save(new Category(name, slug, "Sample products for " + name));
    }

    private void migrateLegacyProduct(Product product, Category category, String name, String slug,
                                      String sku, BigDecimal price, int quantity, String imageUrl) {
        product.migrateSampleData(category, name, slug, sku, price, imageUrl);
        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseGet(() -> new Inventory(product, quantity));
        inventory.changeQuantity(quantity);
        inventoryRepository.save(inventory);
    }

    private Product createProduct(Category category, String name, String slug, String sku,
                                  BigDecimal price, int quantity, String imageUrl) {
        Product product = productRepository.save(new Product(category, name, slug, sku, null, price, imageUrl));
        inventoryRepository.save(new Inventory(product, quantity));
        return product;
    }
}
