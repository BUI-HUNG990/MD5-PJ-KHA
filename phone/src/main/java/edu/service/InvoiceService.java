package edu.service;

import edu.model.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice); // thêm mới + trừ tồn kho
    Invoice updateStatus(Integer invoiceId, Invoice.Status status); // cập nhật trạng thái
    Page<Invoice> findAll(Pageable pageable);
    Page<Invoice> search(String keyword, Pageable pageable);

    BigDecimal getRevenueByDate(LocalDate date);
    BigDecimal getRevenueByMonth(int month, int year);
    BigDecimal getRevenueByYear(int year);
}
