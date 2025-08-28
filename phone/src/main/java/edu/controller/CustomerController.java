package edu.controller;

import edu.model.entity.Customer;
import edu.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<Customer> customerPage = customerService.findPaginated(PageRequest.of(page, size));

        model.addAttribute("customers", customerPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("customer", new Customer());

        return "customers";
    }

    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer, Model model) {
        try {
            customerService.save(customer);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());

            // load lại phân trang khi có lỗi
            Page<Customer> customerPage = customerService.findPaginated(PageRequest.of(0, 5));
            model.addAttribute("customers", customerPage);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("customer", customer);

            return "customers";
        }
        return "redirect:/customers";
    }

    @PostMapping("/update")
    public String updateCustomer(@ModelAttribute Customer customer, Model model) {
        try {
            customerService.update(customer);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());

            Page<Customer> customerPage = customerService.findPaginated(PageRequest.of(0, 5));
            model.addAttribute("customers", customerPage);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", customerPage.getTotalPages());
            model.addAttribute("customer", customer);

            return "customers";
        }
        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id) {
        customerService.delete(id);
        return "redirect:/customers";
    }

    @GetMapping("/search")
    public String searchCustomers(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  Model model) {
        Page<Customer> customers = customerService.searchCustomers(keyword, page);

        model.addAttribute("customers", customers.getContent());
        model.addAttribute("totalPages", customers.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "customers/list";
    }
}
