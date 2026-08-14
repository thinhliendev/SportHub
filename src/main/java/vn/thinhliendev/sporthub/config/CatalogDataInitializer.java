package vn.thinhliendev.sporthub.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.thinhliendev.sporthub.catalog.entity.Category;
import vn.thinhliendev.sporthub.catalog.entity.Product;
import vn.thinhliendev.sporthub.catalog.repository.CategoryRepository;
import vn.thinhliendev.sporthub.catalog.repository.ProductRepository;
import vn.thinhliendev.sporthub.inventory.entity.Inventory;
import vn.thinhliendev.sporthub.inventory.repository.InventoryRepository;

import java.math.BigDecimal;

@Component
@Profile("dev")
public class CatalogDataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public CatalogDataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository,
                                  InventoryRepository inventoryRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (productRepository.count() > 0) {
            return;
        }

        Category running = categoryRepository.save(new Category("Running", "running", "Running shoes and apparel"));
        Category equipment = categoryRepository.save(new Category("Equipment", "equipment", "Training equipment and accessories"));
        Category apparel = categoryRepository.save(new Category("Apparel", "apparel", "Performance sportswear"));

        createProduct(running, "AeroMax Elite Running Shoe", "aeromax-elite-running-shoe", "RUN-AM-001",
                new BigDecimal("189.99"), 12,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDlvim76ZsY4DQQ4qlYxZF_7QZjFiv0Buf-uFMKCpx7ZYBMMpAnl5H3s_dhMCAgm4YJaGWxALyN8lDCwErPS0SBEI_Asn5dA4FHOdYVJF1vN1C548LuM7MgEWpR6ZHh_tbjI8AR-1MoJxQfm9L5B4bahDxLwEbXPSQxkDd-ISNm25HP_jc9_-e8fwxUrmeIbIATYO4EIQDdguPY42xSA9_O6mNe_oJPzlLEI9WRDztUjBUMB99vFs6gRg");
        createProduct(equipment, "Pro-Series Training Duffel", "pro-series-training-duffel", "EQ-DUF-001",
                new BigDecimal("75.00"), 20,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuBuUAIk6RWorw3qODK9sUl310M-RHk23MZqNv8Aqn_jxqzba1Mtdp4ZRO8ud1kNZtdnbZltMUh8CoSpFm9mYoFauErhRlUlDBJazCinnkDAMFOV9_t9bFIv1kEl_Efrj_73P3A9VbQ73xYRd0AxwEHB0yYn30cV6KOdqlJsUWnDBK8JbCV-IZTRqIPWMyN8iS5VINRw8CcwHluXyNwORgv5dIb80YEz4BVmzePaFekgC51ETfg_Kq2jPg");
        createProduct(apparel, "Core Compression Top", "core-compression-top", "APP-TOP-001",
                new BigDecimal("45.00"), 8,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCbnHgE2wpks2H6QnNW-3XZlJp-NWsf7uYHwGKi3pr_5zIWZiRvG0x-m4iCq4zKlzcCXw2-f4iwqhJK6ElnEUs51_szUpDM1nQfs1d8dYz7TMVfse3uuNFGbrdqufV_0PGgF9dC48F5_765EUvyf0hvpHjQdHGelXlBltaU4sP5hQe9fSzaYoprZdTmDKtQkwp0Nx02eiFkNoXBUg2glpTpayi-Daj3ZOFzRuWPlRnHjLwnZItwja3GVg");
        createProduct(apparel, "Sprint Woven Shorts", "sprint-woven-shorts", "APP-SHO-001",
                new BigDecimal("38.00"), 0,
                "https://lh3.googleusercontent.com/aida-public/AB6AXuA_UasQ_yh3WDWS_uZQ2wdyP-ni26u7B3cz_PnaY1TBx6Zue82lJ1PyLUhCGfsUZjfsnvaUBa8k7G_EJ6Hli3xwAAX-59lhcMxSG12P_kurcjwB9AB1HUsZQlFMmkkQ7wVKNdUcbo90RUW6hra3rjjd83flQurjYYi09QNZiBkkjnyT1mKzuTed4uKHHdpq15e58l99gwACUqQTS6YTIWkQ0iRFjgyGEAntDb4Dy-QVZ2syiz8ck9T3GQ");
    }

    private void createProduct(Category category, String name, String slug, String sku,
                               BigDecimal price, int quantity, String imageUrl) {
        Product product = productRepository.save(new Product(category, name, slug, sku, null, price, imageUrl));
        inventoryRepository.save(new Inventory(product, quantity));
    }
}
