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

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        BigDecimal total = BigDecimal.ZERO;

        if (invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
            for (InvoiceDetail d : invoice.getDetails()) {
                Product product = productRepository.findById(d.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

                if (product.getStock() < d.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + product.getName() + " không đủ tồn kho!");
                }


                product.setStock(product.getStock() - d.getQuantity());
                productRepository.save(product);


                d.setUnitPrice(product.getPrice());
                d.setInvoice(invoice);


                total = total.add(product.getPrice()
                        .multiply(BigDecimal.valueOf(d.getQuantity())));
            }
        }

        invoice.setTotalAmount(total);
        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public Invoice updateStatus(Integer invoiceId, Invoice.Status newStatus) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        Invoice.Status oldStatus = invoice.getStatus();


        if (newStatus == Invoice.Status.CANCELED && oldStatus != Invoice.Status.CANCELED) {
            for (InvoiceDetail d : invoice.getDetails()) {
                Product product = d.getProduct();
                product.setStock(product.getStock() + d.getQuantity());
                productRepository.save(product);
            }
        }


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

        invoice.setStatus(newStatus);
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
        BigDecimal revenueByDate = invoiceRepository.getRevenueByDate(date);
        return revenueByDate;
    }

    @Override
    public BigDecimal getRevenueByMonth(int month, int year) {
        BigDecimal revenueByMonth = invoiceRepository.getRevenueByMonth(month, year);
        System.out.println(revenueByMonth);
        return revenueByMonth;
    }

    @Override
    public BigDecimal getRevenueByYear(int year) {
        BigDecimal revenueByYear = invoiceRepository.getRevenueByYear(year);
        System.out.println(revenueByYear);
        return revenueByYear;
    }

    @Override
    public Invoice findById(Long id) {
        return invoiceRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Map<LocalDate, BigDecimal> getRevenueByDaysInMonth(YearMonth yearMonth) {
        Map<LocalDate, BigDecimal> result = new LinkedHashMap<>();
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            BigDecimal revenue = invoiceRepository.getRevenueByDate(date);
            if (revenue != null && revenue.compareTo(BigDecimal.ZERO) > 0) {
                result.put(date, revenue);
            }
        }
        return result;
    }

}