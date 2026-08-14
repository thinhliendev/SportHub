package vn.thinhliendev.sporthub.catalog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.thinhliendev.sporthub.catalog.repository.CategoryRepository;
import vn.thinhliendev.sporthub.catalog.service.ProductService;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductService productService, CategoryRepository categoryRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/products")
    public String listProducts(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("productPage", productService.findActiveProducts(page));
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        return "products/products";
    }
}
