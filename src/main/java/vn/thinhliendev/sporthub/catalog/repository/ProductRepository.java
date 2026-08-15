package vn.thinhliendev.sporthub.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.thinhliendev.sporthub.admin.dto.AdminProductListItem;
import vn.thinhliendev.sporthub.catalog.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = "category")
    Page<Product> findByActiveTrue(Pageable pageable);

    Optional<Product> findBySlugAndActiveTrue(String slug);
    Optional<Product> findBySkuIgnoreCase(String sku);
    Optional<Product> findBySlugIgnoreCase(String slug);
    boolean existsBySkuIgnoreCase(String sku);
    boolean existsBySlugIgnoreCase(String slug);

    @Query(value = """
            select new vn.thinhliendev.sporthub.admin.dto.AdminProductListItem(
                p.id, p.name, p.slug, p.sku, c.name, p.price, i.quantity, p.active, p.updatedAt)
            from Product p
            join p.category c
            join Inventory i on i.product = p
            where (:keyword = ''
                or lower(p.name) like lower(concat('%', :keyword, '%'))
                or lower(p.sku) like lower(concat('%', :keyword, '%')))
              and (:stockFilter = 'ALL'
                or (:stockFilter = 'IN_STOCK' and i.quantity > 5)
                or (:stockFilter = 'LOW_STOCK' and i.quantity between 1 and 5)
                or (:stockFilter = 'OUT_OF_STOCK' and i.quantity = 0))
            """,
            countQuery = """
            select count(p)
            from Product p
            join p.category c
            join Inventory i on i.product = p
            where (:keyword = ''
                or lower(p.name) like lower(concat('%', :keyword, '%'))
                or lower(p.sku) like lower(concat('%', :keyword, '%')))
              and (:stockFilter = 'ALL'
                or (:stockFilter = 'IN_STOCK' and i.quantity > 5)
                or (:stockFilter = 'LOW_STOCK' and i.quantity between 1 and 5)
                or (:stockFilter = 'OUT_OF_STOCK' and i.quantity = 0))
            """)
    Page<AdminProductListItem> findForAdmin(@Param("keyword") String keyword,
                                            @Param("stockFilter") String stockFilter,
                                            Pageable pageable);
}
