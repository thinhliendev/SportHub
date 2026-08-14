package vn.thinhliendev.sporthub.catalog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import vn.thinhliendev.sporthub.catalog.entity.Category;
import vn.thinhliendev.sporthub.catalog.entity.Product;
import vn.thinhliendev.sporthub.catalog.repository.ProductRepository;
import vn.thinhliendev.sporthub.inventory.entity.Inventory;
import vn.thinhliendev.sporthub.inventory.repository.InventoryRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock InventoryRepository inventoryRepository;
    @Mock Product product;
    @Mock Category category;
    @Mock Inventory inventory;

    @Test
    void returnsActiveProductsWithInventoryAndNormalizesNegativePage() {
        when(product.getId()).thenReturn(10L);
        when(product.getName()).thenReturn("Training Ball");
        when(product.getSlug()).thenReturn("training-ball");
        when(product.getSku()).thenReturn("BALL-001");
        when(product.getCategory()).thenReturn(category);
        when(category.getName()).thenReturn("Equipment");
        when(product.getPrice()).thenReturn(new BigDecimal("25.00"));
        when(product.getImageUrl()).thenReturn("/images/ball.jpg");
        when(inventory.getProduct()).thenReturn(product);
        when(inventory.getQuantity()).thenReturn(5);
        when(productRepository.findByActiveTrue(org.mockito.ArgumentMatchers.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(inventoryRepository.findByProductIdIn(anyCollection())).thenReturn(List.of(inventory));

        var result = new ProductService(productRepository, inventoryRepository).findActiveProducts(-2);

        assertThat(result.getContent()).singleElement().satisfies(item -> {
            assertThat(item.name()).isEqualTo("Training Ball");
            assertThat(item.categoryName()).isEqualTo("Equipment");
            assertThat(item.quantity()).isEqualTo(5);
            assertThat(item.inStock()).isTrue();
        });
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findByActiveTrue(pageable.capture());
        assertThat(pageable.getValue().getPageNumber()).isZero();
        assertThat(pageable.getValue().getPageSize()).isEqualTo(9);
    }
}
