package edu.repo;

import edu.model.entity.Invoice;
import edu.model.entity.Invoice.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    @Query("""
        SELECT i FROM Invoice i
        WHERE (LOWER(i.customer.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR CAST(i.createdAt AS string) LIKE %:keyword%)
        """)
    Page<Invoice> search(String keyword, Pageable pageable);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE DATE(i.createdAt) = :date AND i.status='COMPLETED'")
    BigDecimal getRevenueByDate(@Param("date") LocalDate date);


    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE MONTH(i.createdAt) = :month AND YEAR(i.createdAt) = :year AND i.status='COMPLETED'")
    BigDecimal getRevenueByMonth(@Param("month") int month,@Param("year") int year);


    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE YEAR(i.createdAt) = :year AND i.status='COMPLETED'")
    BigDecimal getRevenueByYear(@Param("year") int year);
}