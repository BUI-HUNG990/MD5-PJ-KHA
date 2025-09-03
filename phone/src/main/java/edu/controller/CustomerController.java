package edu.controller;

import edu.model.entity.Customer;
import edu.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
// tất cả các API trong class này đều bắt đầu với customers
public class CustomerController {

    private final CustomerService customerService;
    // service xử lý nghiệp vụ liên quan đến khách hàng

    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            // số trang hiện tại (mặc định = 0)
            @RequestParam(defaultValue = "5") int size,
            // số bản ghi mỗi trang (mặc định = 5)
            Model model) {

        // gọi service để lấy danh sách khách hàng phân trang
        Page<Customer> customerPage = customerService.findPaginated(PageRequest.of(page, size));

        // đưa dữ liệu ra view
        model.addAttribute("customers", customerPage);
        // danh sách khách hàng (có phân trang)
        model.addAttribute("currentPage", page);
        // trang hiện tại
        model.addAttribute("totalPages", customerPage.getTotalPages());
        // tổng số trang
        model.addAttribute("customer", new Customer());
        // object Customer trống để binding form

        return "customers"; // trả về view customers.html
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer, Model model) {
        try {
            // lưu khách hàng mới vào DB
            customerService.save(customer);
        } catch (RuntimeException ex) {
            // nếu có lỗi gửi thông báo ra view
            model.addAttribute("errorMessage", ex.getMessage());

            // nạp lại danh sách khách hàng để hiển thị
            Page<Customer> customerPage = customerService.findPaginated(PageRequest.of(0, 5));
            model.addAttribute("customers", customerPage);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("customer", customer);

            return "customers"; // quay lại trang customers với thông báo lỗi
        }
        return "redirect:/customers";
        // nếu thành công load lại danh sách
    }

    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute Customer customer, Model model) {
        try {
            // cập nhật thông tin khách hàng
            customerService.update(customer);
        } catch (RuntimeException ex) {
            // nếu có lỗi hiển thị lại thông báo
            model.addAttribute("errorMessage", ex.getMessage());

            // nạp lại danh sách khách hàng để hiển thị
            Page<Customer> customerPage = customerService.findPaginated(PageRequest.of(0, 5));
            model.addAttribute("customers", customerPage);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("customer", customer);

            return "customers"; // quay lại trang customers với thông báo lỗi
        }
        return "redirect:/customers"; // nếu thành công load lại danh sách
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        // kiểm tra khách hàng có hóa đơn chưa
        if (customerService.hasInvoice(id)) {
            // nếu đã có hóa đơn thì không cho xóa
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa, khách hàng đã có hóa đơn.");
        } else {
            // nếu chưa có hóa đơn thì cho phép xóa
            customerService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        }
        return "redirect:/customers";
        // quay lại danh sách khách hàng
    }

    @GetMapping("/search")
    public String searchCustomers(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  Model model) {
        // gọi service tìm kiếm khách hàng theo keyword có phân trang)
        Page<Customer> customers = customerService.searchCustomers(keyword, page);

        // đưa dữ liệu ra view
        model.addAttribute("customers", customers.getContent());
        // danh sách khách hàng tìm được
        model.addAttribute("totalPages", customers.getTotalPages());
        // tổng số trang
        model.addAttribute("currentPage", page);
        // trang hiện tại
        model.addAttribute("keyword", keyword);
        // giữ lại từ khóa tìm kiếm

        return "customers/list";
        // trả về view customers/list.html
    }
}
