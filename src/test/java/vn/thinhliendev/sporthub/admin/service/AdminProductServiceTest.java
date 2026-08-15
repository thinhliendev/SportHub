package vn.thinhliendev.sporthub.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.thinhliendev.sporthub.admin.dto.ProductCreateForm;
import vn.thinhliendev.sporthub.catalog.entity.Category;
import vn.thinhliendev.sporthub.catalog.entity.Product;
import vn.thinhliendev.sporthub.catalog.repository.CategoryRepository;
import vn.thinhliendev.sporthub.catalog.repository.ProductRepository;
import vn.thinhliendev.sporthub.inventory.entity.Inventory;
import vn.thinhliendev.sporthub.inventory.repository.InventoryRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {

    @Mock ProductRepository productRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock InventoryRepository inventoryRepository;

    private AdminProductService service;

    @BeforeEach
    void setUp() {
        service = new AdminProductService(productRepository, categoryRepository, inventoryRepository);
    }

    @Test
    void createsProductAndInitialInventoryTogether() {
        ProductCreateForm form = validForm();
        Category category = new Category("Running", "running", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product product = service.createProduct(form);

        assertThat(product.getSku()).isEqualTo("RUN-001");
        assertThat(product.getSlug()).isEqualTo("running-shoe");
        assertThat(product.getName()).isEqualTo("Running Shoe");
        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getProduct()).isSameAs(product);
        assertThat(inventoryCaptor.getValue().getQuantity()).isEqualTo(10);
    }

    @Test
    void rejectsDuplicateSkuBeforeSavingAnything() {
        ProductCreateForm form = validForm();
        when(productRepository.existsBySkuIgnoreCase("RUN-001")).thenReturn(true);

        assertThatThrownBy(() -> service.createProduct(form))
                .isInstanceOf(ProductAlreadyExistsException.class)
                .hasMessage("This SKU is already in use");
        verify(productRepository, never()).save(any());
        verify(inventoryRepository, never()).save(any());
    }

    private ProductCreateForm validForm() {
        ProductCreateForm form = new ProductCreateForm();
        form.setName(" Running Shoe ");
        form.setSlug("running-shoe");
        form.setSku("run-001");
        form.setCategoryId(1L);
        form.setPrice(new BigDecimal("99.99"));
        form.setQuantity(10);
        return form;
    }
}
