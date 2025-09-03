package edu.service.impl;

import edu.model.entity.Invoice;
import edu.model.entity.InvoiceDetail;
import edu.model.entity.Product;
import edu.repo.InvoiceRepository;
import edu.repo.ProductRepository;
import edu.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service // đánh dấu class này là 1 Spring Service
@RequiredArgsConstructor // tự động tạo constructor với các field final
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    // repository thao tác bảng Invoice
    private final ProductRepository productRepository;
    // repository thao tác bảng Product

    @Override
    @Transactional // đảm bảo toàn bộ quá trình tạo hóa đơn chạy trong 1 transaction
    public Invoice createInvoice(Invoice invoice) {
        BigDecimal total = BigDecimal.ZERO;
        // tổng tiền hóa đơn ban đầu = 0

        // kiểm tra danh sách chi tiết hóa đơn InvoiceDetail
        if (invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
            for (InvoiceDetail d : invoice.getDetails()) {
                // lấy thông tin sản phẩm từ DB
                Product product = productRepository.findById(d.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

                // kiểm tra tồn kho.
                if (product.getStock() < d.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ tồn kho!");
                }

                // giảm số lượng tồn kho sau khi bán
                product.setStock(product.getStock() - d.getQuantity());
                productRepository.save(product);

                // ghi lại giá sản phẩm vào chi tiết hóa đơn
                d.setUnitPrice(product.getPrice());
                d.setInvoice(invoice);

                // cộng dồn tổng tiền = giá * số lượng
                total = total.add(product.getPrice()
                        .multiply(BigDecimal.valueOf(d.getQuantity())));
            }
        }

        // lưu tổng tiền vào hóa đơn
        invoice.setTotalAmount(total);
        return invoiceRepository.save(invoice);
        // lưu hóa đơn xuống DB.
    }

    @Override
    @Transactional // transaction khi cập nhật trạng thái hóa đơn.
    public Invoice updateStatus(Integer invoiceId, Invoice.Status newStatus) {
        // tìm hóa đơn theo ID.
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        Invoice.Status oldStatus = invoice.getStatus();
        // trạng thái cũ.

        // nếu hủy hóa đơn thì hoàn lại hàng vào tồn kho.
        if (newStatus == Invoice.Status.CANCELED && oldStatus != Invoice.Status.CANCELED) {
            for (InvoiceDetail d : invoice.getDetails()) {
                Product product = d.getProduct();
                product.setStock(product.getStock() + d.getQuantity());
                productRepository.save(product);
            }
        }

        // nếu khôi phục hóa đơn từ trạng thái đã hủy trừ hàng khỏi tồn kho.
        if (oldStatus == Invoice.Status.CANCELED && newStatus != Invoice.Status.CANCELED) {
            for (InvoiceDetail d : invoice.getDetails()) {
                Product product = d.getProduct();
                if (product.getStock() < d.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ tồn kho để khôi phục đơn hàng!");
                }
                product.setStock(product.getStock() - d.getQuantity());
                productRepository.save(product);
            }
        }

        // cập nhật trạng thái hóa đơn.
        invoice.setStatus(newStatus);
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
        // lấy danh sách hóa đơn có phân trang.
    }

    @Override
    public Page<Invoice> search(String keyword, Pageable pageable) {
        return invoiceRepository.search(keyword, pageable);
        // tìm kiếm hóa đơn theo từ khóa.
    }

    @Override
    public BigDecimal getRevenueByDate(LocalDate date) {
        BigDecimal revenueByDate = invoiceRepository.getRevenueByDate(date);
        // doanh thu theo ngày.
        return revenueByDate;
    }

    @Override
    public BigDecimal getRevenueByMonth(int month, int year) {
        BigDecimal revenueByMonth = invoiceRepository.getRevenueByMonth(month, year);
        // doanh thu theo tháng.
        System.out.println(revenueByMonth); // In ra log (debug).
        return revenueByMonth;
    }

    @Override
    public BigDecimal getRevenueByYear(int year) {
        BigDecimal revenueByYear = invoiceRepository.getRevenueByYear(year);
        // doanh thu theo năm.
        System.out.println(revenueByYear);
        return revenueByYear;
    }

    @Override
    public Invoice findById(Long id) {
        // tìm hóa đơn theo ID chuyển từ Long sang Integer
        return invoiceRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
        // lấy tất cả hóa đơn.
    }

    // doanh thu theo từng ngày trong tháng.
    public Map<LocalDate, BigDecimal> getRevenueByDaysInMonth(YearMonth yearMonth) {
        Map<LocalDate, BigDecimal> result = new LinkedHashMap<>();
        LocalDate start = yearMonth.atDay(1);
        // ngày đầu tháng.
        LocalDate end = yearMonth.atEndOfMonth();
        // ngày cuối tháng.

        // duyệt từng ngày trong tháng.
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            BigDecimal revenue = invoiceRepository.getRevenueByDate(date);
            // chỉ thêm vào nếu doanh thu > 0.
            if (revenue != null && revenue.compareTo(BigDecimal.ZERO) > 0) {
                result.put(date, revenue);
            }
        }
        return result;
        // trả về map chứa doanh thu theo từng ngày.
    }

}
