package vn.thinhliendev.sporthub.admin.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.thinhliendev.sporthub.admin.dto.ProductCreateForm;
import vn.thinhliendev.sporthub.admin.dto.StockFilter;
import vn.thinhliendev.sporthub.admin.service.AdminProductService;
import vn.thinhliendev.sporthub.admin.service.ProductAlreadyExistsException;
import vn.thinhliendev.sporthub.catalog.repository.CategoryRepository;

@Controller
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final CategoryRepository categoryRepository;

    public AdminProductController(AdminProductService adminProductService,
                                  CategoryRepository categoryRepository) {
        this.adminProductService = adminProductService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/admin/products")
    public String listProducts(@RequestParam(defaultValue = "") String keyword,
                               @RequestParam(defaultValue = "ALL") String stock,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        StockFilter stockFilter = StockFilter.from(stock);
        model.addAttribute("productPage", adminProductService.findProducts(keyword, stockFilter, page));
        model.addAttribute("keyword", keyword.trim());
        model.addAttribute("selectedStock", stockFilter);
        model.addAttribute("stockFilters", StockFilter.values());
        return "admin/products/product-list";
    }

    @GetMapping("/admin/products/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new ProductCreateForm());
        }
        addCategories(model);
        return "admin/products/product-form";
    }

    @PostMapping("/admin/products")
    public String createProduct(@Valid @ModelAttribute("productForm") ProductCreateForm productForm,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addCategories(model);
            return "admin/products/product-form";
        }
        try {
            adminProductService.createProduct(productForm);
        } catch (ProductAlreadyExistsException exception) {
            bindingResult.rejectValue(exception.getField(), "product.duplicate", exception.getMessage());
            addCategories(model);
            return "admin/products/product-form";
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("product.category", exception.getMessage());
            addCategories(model);
            return "admin/products/product-form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Product created successfully");
        return "redirect:/admin/products";
    }

    private void addCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
    }
}
