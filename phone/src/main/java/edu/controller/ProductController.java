package edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.model.entity.Product;
import edu.service.ProductService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        Page<Product> products = productService.findAll(PageRequest.of(page, size));
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        return "product/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/add";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product) {
        productService.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            Product product = productService.findById(id);
            model.addAttribute("product", product);
            return "product/edit";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/products";
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Product product) {
        productService.update(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        productService.delete(id);
        return "redirect:/products";
    }

    // ✅ Tìm kiếm theo brand (phân trang)
    @GetMapping("/search/brand")
    public String searchByBrand(@RequestParam String brand,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model) {
        Page<Product> products = productService.searchByBrand(brand, PageRequest.of(page, size));
        model.addAttribute("products", products);
        model.addAttribute("searchType", "brand");
        model.addAttribute("keyword", brand);
        return "product/list";
    }

    // ✅ Tìm kiếm theo khoảng giá (phân trang)
    @GetMapping("/search/price")
    public String searchByPrice(@RequestParam BigDecimal min,
                                @RequestParam BigDecimal max,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model) {
        Page<Product> products = productService.searchByPriceRange(min, max, PageRequest.of(page, size));
        model.addAttribute("products", products);
        model.addAttribute("searchType", "price");
        model.addAttribute("min", min);
        model.addAttribute("max", max);
        return "product/list";
    }

    // ✅ Tìm kiếm theo tồn kho (phân trang)
    @GetMapping("/search/stock")
    public String searchByStock(@RequestParam Integer stock,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model) {
        Page<Product> products = productService.searchByStock(stock, PageRequest.of(page, size));
        model.addAttribute("products", products);
        model.addAttribute("searchType", "stock");
        model.addAttribute("stock", stock);
        return "product/list";
    }
}
