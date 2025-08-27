package edu.repo;

import edu.model.entity.Invoice;
import edu.model.entity.Invoice.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    // Tìm kiếm gần đúng theo tên KH hoặc ngày tạo
    @Query("""
        SELECT i FROM Invoice i
        WHERE (LOWER(i.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR CAST(i.createdAt AS string) LIKE %:keyword%)
        """)
    Page<Invoice> search(String keyword, Pageable pageable);

    // Tổng doanh thu theo ngày
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE DATE(i.createdAt) = :date AND i.status='COMPLETED'")
    BigDecimal getRevenueByDate(LocalDate date);

    // Tổng doanh thu theo tháng
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE MONTH(i.createdAt) = :month AND YEAR(i.createdAt) = :year AND i.status='COMPLETED'")
    BigDecimal getRevenueByMonth(int month, int year);

    // Tổng doanh thu theo năm
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE YEAR(i.createdAt) = :year AND i.status='COMPLETED'")
    BigDecimal getRevenueByYear(int year);
}
