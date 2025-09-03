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
// tất cả endpoint trong class này đều bắt đầu bằng invoices
public class InvoiceController {

    private final InvoiceService invoiceService;
    // service xử lý logic hóa đơn
    private final CustomerRepository customerRepository;
    // repository thao tác dữ liệu khách hàng
    private final ProductRepository productRepository;
    // repository thao tác dữ liệu sản phẩm

    @GetMapping
    public String listInvoices(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               // trang hiện tại
                               @RequestParam(defaultValue = "5") int size,
                               // Số hóa đơn mỗi trang
                               @RequestParam(required = false) String keyword) {

        Page<Invoice> invoicePage;

        // nếu có keyword thì tìm kiếm theo từ khóa
        if (keyword != null && !keyword.isBlank()) {
            invoicePage = invoiceService.search(keyword, PageRequest.of(page, size));
            model.addAttribute("keyword", keyword);
        } else {
            // nếu không có keyword thì lấy toàn bộ hóa đơn có phân trang)
            invoicePage = invoiceService.findAll(PageRequest.of(page, size));
        }

        // truyền dữ liệu ra view
        model.addAttribute("invoices", invoicePage.getContent());
        // danh sách hóa đơn
        model.addAttribute("totalPages", invoicePage.getTotalPages());
        // tổng số trang
        model.addAttribute("currentPage", page);
        // trang hiện tại

        // đổ thêm dữ liệu khách hàng và sản phẩm để chọn khi tạo hóa đơn
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("products", productRepository.findAll());

        return "invoice"; // trả về trang invoice.html
    }

    @PostMapping("/save")
    public String saveInvoice(@RequestParam("customerId") Integer customerId,
                              @ModelAttribute Invoice invoice) {
        // tìm khách hàng theo id
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // thêm khách hàng vào hóa đơn
        invoice.setCustomer(customer);

        // lưu hóa đơn qua service
        invoiceService.createInvoice(invoice);

        return "redirect:/invoices";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Integer id, @RequestParam Invoice.Status status) {
        // Cập nhật trạng thái hóa đơn PENDING, CONFIRMED, ...
        invoiceService.updateStatus(id, status);
        return "redirect:/invoices";
    }

    @GetMapping("/details/{id}")
    public String invoiceDetails(@PathVariable("id") int id, Model model) {
        // lấy hóa đơn theo id dùng service rồi lọc trong danh sách
        Invoice invoice = invoiceService.getAllInvoices().stream()
                .filter(i -> i.getId() == id)
                .findFirst()
                .orElse(null);

        if (invoice == null) {
            // nếu không có thì quay lại danh sách hóa đơn
            return "redirect:/invoices";
        }

        // truyền hóa đơn và chi tiết hóa đơn ra view
        model.addAttribute("invoice", invoice);
        model.addAttribute("details", invoice.getDetails());
        return "invoice_details";
        // trả về view chi tiết hóa đơn
    }

    @PostMapping("/invoices/add")
    public String addInvoice(@ModelAttribute Invoice invoice) {
        // thêm hóa đơn mới
        invoiceService.createInvoice(invoice);
        return "redirect:/invoices";
    }

    @GetMapping("/statistics")
    public String statistics(Model model,
                             @RequestParam(required = false) String day,
                             // tìm theo ngày dd/MM/yyyy
                             @RequestParam(required = false) String month,
                             // tìm theo tháng MM/yyyy
                             @RequestParam(required = false) String year)
    // tìm theo năm yyyy
    {
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MM/yyyy");

        // doanh thu theo ngày
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

        // doanh thu theo tháng
        if (month != null && !month.isBlank()) {
            try {
                YearMonth yearMonth = YearMonth.parse(month, monthFormat);

                // lấy doanh thu theo từng ngày trong tháng
                Map<LocalDate, BigDecimal> dailyRevenues = invoiceService.getRevenueByDaysInMonth(yearMonth);

                // tổng doanh thu cả tháng
                BigDecimal totalRevenue = dailyRevenues.values()
                        .stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                model.addAttribute("month", month);
                model.addAttribute("dailyRevenues", dailyRevenues);
                // map<ngày, doanh thu>
                model.addAttribute("revenueByMonth", totalRevenue);
                // tổng doanh thu tháng
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("revenueByMonth", null);
            }
        }

        // doanh thu theo năm
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
        // trả về view thống kê
    }
}
