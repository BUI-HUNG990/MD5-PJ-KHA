package edu.controller;

import edu.model.entity.Customer;
import edu.model.entity.Invoice;
import edu.repo.CustomerRepository;
import edu.repo.ProductRepository;
import edu.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

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

        model.addAttribute("invoices", invoicePage.getContent());
        model.addAttribute("totalPages", invoicePage.getTotalPages());
        model.addAttribute("currentPage", page);

        model.addAttribute("customers", customerRepository.findAll());

        model.addAttribute("products", productRepository.findAll());

        return "invoice";
    }

    @PostMapping("/save")
    public String saveInvoice(@RequestParam("customerId") Integer customerId,
                              @ModelAttribute Invoice invoice) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        invoice.setCustomer(customer);
        invoiceService.createInvoice(invoice);

        return "redirect:/invoices";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Integer id, @RequestParam Invoice.Status status) {
        invoiceService.updateStatus(id, status);
        return "redirect:/invoices";
    }

    @GetMapping("/details/{id}")
    public String invoiceDetails(@PathVariable("id") int id, Model model) {
        Invoice invoice = invoiceService.getAllInvoices().stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);

        if (invoice == null) {
            return "redirect:/invoices";
        }

        model.addAttribute("invoice", invoice);
        model.addAttribute("details", invoice.getDetails());
        return "invoice_details";
    }

    @PostMapping("/invoices/add")
    public String addInvoice(@ModelAttribute Invoice invoice) {
        invoiceService.createInvoice(invoice);
        return "redirect:/invoices";
    }

    @GetMapping("/statistics")
    public String statistics(Model model,
                             @RequestParam(required = false) String day,
                             @RequestParam(required = false) String month,
                             @RequestParam(required = false) String year) {

        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM/yyyy");


        if (day != null && !day.isBlank()) {
            try {
                LocalDate searchDay = LocalDate.parse(day, dayFormat);
                model.addAttribute("revenueByDay", invoiceService.getRevenueByDate(searchDay));
                model.addAttribute("day", day);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                model.addAttribute("revenueByDay", null);
            }
        }


        if (month != null && !month.isBlank()) {
            try {
                YearMonth yearMonth = YearMonth.parse(month, monthFormat);


                Map<LocalDate, BigDecimal> dailyRevenues = invoiceService.getRevenueByDaysInMonth(yearMonth);


                BigDecimal totalRevenue = dailyRevenues.values()
                        .stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                model.addAttribute("month", month);
                model.addAttribute("dailyRevenues", dailyRevenues);
                model.addAttribute("revenueByMonth", totalRevenue);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("revenueByMonth", null);
            }
        }


        if (year != null && !year.isBlank()) {
            try {
                int y = Integer.parseInt(year);
                model.addAttribute("revenueByYearSearch", invoiceService.getRevenueByYear(y));
                model.addAttribute("year", year);
            } catch (NumberFormatException e) {
                model.addAttribute("revenueByYearSearch", null);
            }
        }

        return "statistics";
    }

}