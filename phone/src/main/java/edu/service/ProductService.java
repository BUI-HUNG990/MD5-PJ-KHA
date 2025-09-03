package edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import edu.model.entity.Product;

import java.math.BigDecimal;

public interface ProductService {
    // lưu sản phẩm mới vào CSDL
    Product save(Product product);

    // cập nhật thông tin sản phẩm đã có
    Product update(Product product);

    // xóa sản phẩm theo id
    void delete(Integer id);

    // lấy danh sách sản phẩm có phân trang
    Page<Product> findAll(Pageable pageable);

    // ìtm sản phẩm theo thương hiệu brand kèm phân trang
    Page<Product> searchByBrand(String brand, Pageable pageable);

    // tìm sản phẩm theo khoảng giá min - max kèm phân trang
    Page<Product> searchByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable);

    // tìm sản phẩm theo số lượng tồn kho stock kèm phân trang
    Page<Product> searchByStock(Integer stock, Pageable pageable);

    // tìm sản phẩm theo id
    Product findById(Integer id);
}
