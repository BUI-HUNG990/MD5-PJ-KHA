package edu.service.impl;

import edu.model.entity.*;
import edu.repo.InvoiceRepository;
import edu.repo.ProductRepository;
import edu.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        // Tính tổng tiền + trừ tồn kho
        BigDecimal total = invoice.getTotalAmount();

        if (invoice.getDetails() != null) {
            total = BigDecimal.ZERO;
            for (InvoiceDetail d : invoice.getDetails()) {
                var product = productRepository.findById(d.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (product.getStock() < d.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ tồn kho!");
                }

                product.setStock(product.getStock() - d.getQuantity());
                productRepository.save(product);

                d.setInvoice(invoice);
                d.setUnitPrice(product.getPrice());
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())));
            }
        }

        invoice.setTotalAmount(total);
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public Invoice updateStatus(Integer invoiceId, Invoice.Status status) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Nếu hủy đơn → trả lại tồn kho
        if (status == Invoice.Status.CANCELED && invoice.getStatus() != Invoice.Status.CANCELED) {
            for (InvoiceDetail d : invoice.getDetails()) {
                var product = d.getProduct();
                product.setStock(product.getStock() + d.getQuantity());
                productRepository.save(product);
            }
        }

        invoice.setStatus(status);
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> findAll(Pageable pageable) {
        return invoiceRepository.findAll(pageable);
    }

    @Override
    public Page<Invoice> search(String keyword, Pageable pageable) {
        return invoiceRepository.search(keyword, pageable);
    }

    @Override
    public BigDecimal getRevenueByDate(LocalDate date) {
        return invoiceRepository.getRevenueByDate(date);
    }

    @Override
    public BigDecimal getRevenueByMonth(int month, int year) {
        return invoiceRepository.getRevenueByMonth(month, year);
    }

    @Override
    public BigDecimal getRevenueByYear(int year) {
        return invoiceRepository.getRevenueByYear(year);
    }
}
