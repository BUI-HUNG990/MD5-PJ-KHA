package edu.service;

import edu.model.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    Invoice updateStatus(Integer invoiceId, Invoice.Status status);
    Page<Invoice> findAll(Pageable pageable);
    Page<Invoice> search(String keyword, Pageable pageable);

    BigDecimal getRevenueByDate(LocalDate date);
    BigDecimal getRevenueByMonth(int month, int year);
    BigDecimal getRevenueByYear(int year);

    Invoice findById(Long id);

    List<Invoice> getAllInvoices();
}