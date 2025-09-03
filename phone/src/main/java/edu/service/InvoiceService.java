package edu.service;

import edu.model.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface InvoiceService {
    // tạo mới một hóa đơn
    Invoice createInvoice(Invoice invoice);

    // cập nhật trạng thái của hóa đơn PENDING, CONFIRMED, CANCELED
    Invoice updateStatus(Integer invoiceId, Invoice.Status status);

    // lấy danh sách hóa đơn có phân trang
    Page<Invoice> findAll(Pageable pageable);

    // tìm kiếm hóa đơn theo từ khóa với phân trang
    Page<Invoice> search(String keyword, Pageable pageable);

    // tính tổng doanh thu theo ngày cụ thể
    BigDecimal getRevenueByDate(LocalDate date);

    // tính tổng doanh thu theo tháng và năm
    BigDecimal getRevenueByMonth(int month, int year);

    // tính tổng doanh thu theo năm
    BigDecimal getRevenueByYear(int year);

    // tìm hóa đơn theo id
    Invoice findById(Long id);

    // lấy toàn bộ danh sách hóa đơn
    List<Invoice> getAllInvoices();

    // lấy doanh thu theo từng ngày trong một tháng cụ thể dùng cho biểu đồ, thống kê
    Map<LocalDate, BigDecimal> getRevenueByDaysInMonth(YearMonth yearMonth);
}
