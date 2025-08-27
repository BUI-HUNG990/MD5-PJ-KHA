package edu.controller;

import edu.model.entity.Customer;
import edu.model.entity.Invoice;
import edu.service.InvoiceService;
import edu.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final CustomerRepository customerRepository;

    // Danh sách hóa đơn
    @GetMapping
    public String listInvoices(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(required = false) String keyword) {
        Page<Invoice> invoicePage;
        if (keyword != null && !keyword.isBlank()) {
            invoicePage = invoiceService.search(keyword, PageRequest.of(page, size));
            model.addAttribute("keyword", keyword);
        } else {
            invoicePage = invoiceService.findAll(PageRequest.of(page, size));
        }

        model.addAttribute("invoices", invoicePage);
        model.addAttribute("totalPages", invoicePage.getTotalPages());
        model.addAttribute("currentPage", page);

        // 👇 Thêm danh sách khách hàng cho modal
        model.addAttribute("customers", customerRepository.findAll());

        return "invoices"; // invoices.html
    }

    // Thêm hóa đơn
    @PostMapping("/save")
    public String saveInvoice(@RequestParam("customerId") Integer customerId,
                              @ModelAttribute Invoice invoice) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        invoice.setCustomer(customer);
        invoiceService.createInvoice(invoice);

        return "redirect:/invoices";
    }

    // Cập nhật trạng thái
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Integer id, @RequestParam Invoice.Status status) {
        invoiceService.updateStatus(id, status);
        return "redirect:/invoices";
    }
}
