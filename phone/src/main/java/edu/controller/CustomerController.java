package edu.controller;

import edu.model.entity.Customer;
import edu.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    // ✅ Hiển thị danh sách khách hàng
    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("customer", new Customer()); // dùng cho form thêm mới
        return "customers"; // trỏ tới customers.html
    }

    // ✅ Thêm khách hàng mới, bắt lỗi duplicate
    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer, Model model) {
        try {
            customerService.save(customer);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("customer", customer); // giữ dữ liệu đã nhập
            return "customers";
        }
        return "redirect:/customers";
    }

    // ✅ Cập nhật khách hàng, bắt lỗi duplicate
    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute Customer customer, Model model) {
        try {
            customerService.update(customer);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("customer", customer);
            return "customers";
        }
        return "redirect:/customers";
    }

    // ✅ Xóa khách hàng
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id) {
        customerService.delete(id);
        return "redirect:/customers";
    }



}
