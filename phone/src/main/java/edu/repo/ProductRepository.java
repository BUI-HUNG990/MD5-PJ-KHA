package edu.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.model.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByBrandContainingIgnoreCase(String brand, Pageable pageable);
    Page<Product> findByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);
    Page<Product> findByStockGreaterThanEqual(Integer stock, Pageable pageable);
    // ✅ Tìm kiếm theo Brand gần đúng
    List<Product> findByBrandContainingIgnoreCase(String brand);

    // ✅ Tìm kiếm theo khoảng giá
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

    // ✅ Tìm kiếm theo tồn kho
    @Query("SELECT p FROM Product p WHERE (:inStock = true AND p.stock > 0) OR (:inStock = false AND p.stock = 0)")
    List<Product> findByStockAvailability(@Param("inStock") boolean inStock);
}
