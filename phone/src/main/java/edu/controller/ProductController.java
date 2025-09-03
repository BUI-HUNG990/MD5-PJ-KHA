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
// tất cả endpoint trong controller này đều bắt đầu bằng products
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    // service xử lý logic liên quan đến sản phẩm

    @GetMapping
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               // trang hiện tại
                               @RequestParam(defaultValue = "5") int size,
                               // số sản phẩm mỗi trang
                               Model model) {
        // lấy danh sách sản phẩm có phân trang
        Page<Product> products = productService.findAll(PageRequest.of(page, size));

        // truyền dữ liệu ra view
        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());

        return "product/list";
        // trả về trang danh sách sản phẩm
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        // tạo đối tượng rỗng để binding dữ liệu từ form
        model.addAttribute("product", new Product());
        return "product/add";
        // trả về form thêm sản phẩm
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product) {
        // lưu sản phẩm mới
        productService.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        try {
            // tìm sản phẩm theo id
            Product product = productService.findById(id);
            model.addAttribute("product", product);
            return "product/edit";
            // trả về form chỉnh sửa
        } catch (Exception e) {
            // nếu không tìm thấy thì trả về danh sách kèm thông báo lỗi
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/products";
        }
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Product product) {
        // cập nhật sản phẩm
        productService.update(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        // xóa sản phẩm theo id
        productService.delete(id);
        return "redirect:/products";
    }

    @GetMapping("/search/brand")
    public String searchByBrand(@RequestParam String brand,
                                // tìm theo thương hiệu
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model) {
        // tìm kiếm sản phẩm theo brand (phân trang)
        Page<Product> products = productService.searchByBrand(brand, PageRequest.of(page, size));

        // truyền dữ liệu ra view
        model.addAttribute("products", products);
        model.addAttribute("searchType", "brand");
        model.addAttribute("keyword", brand);

        return "product/list";
    }

    @GetMapping("/search/price")
    public String searchByPrice(@RequestParam BigDecimal min,
                                // giá thấp nhất
                                @RequestParam BigDecimal max,
                                // giá cao nhất
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model) {
        // tìm kiếm sản phẩm theo khoảng giá
        Page<Product> products = productService.searchByPriceRange(min, max, PageRequest.of(page, size));

        // truyền dữ liệu ra view
        model.addAttribute("products", products);
        model.addAttribute("searchType", "price");
        model.addAttribute("min", min);
        model.addAttribute("max", max);

        return "product/list";
    }

    @GetMapping("/search/stock")
    public String searchByStock(@RequestParam Integer stock,
                                // số lượng tồn kho
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model) {
        // tìm kiếm sản phẩm theo số lượng tồn kho
        Page<Product> products = productService.searchByStock(stock, PageRequest.of(page, size));

        // truyền dữ liệu ra view
        model.addAttribute("products", products);
        model.addAttribute("searchType", "stock");
        model.addAttribute("stock", stock);

        return "product/list";
    }
}
