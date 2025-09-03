package edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import edu.model.entity.Product;
import edu.repo.ProductRepository;
import edu.service.ProductService;

import java.math.BigDecimal;

@Service // đánh dấu lớp này là 1 Service trong Spring
@RequiredArgsConstructor // tự động sinh constructor chứa các dependency final
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository; // repository thao tác với bảng Product.

    @Override
    public Product save(Product product) {
        // nếu product chưa có ảnh thì gán chuỗi rỗng để tránh null.
        if (product.getImage() == null) {
            product.setImage("");
        }
        return productRepository.save(product);
        // lưu sản phẩm mới xuống DB.
    }

    @Override
    public Product update(Product product) {
        // tìm sản phẩm trong DB theo ID.
        Product existing = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // cập nhật lại thông tin từ input vào đối tượng đã có.
        existing.setName(product.getName());
        existing.setBrand(product.getBrand());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());

        // lưu lại sản phẩm đã cập nhật.
        return productRepository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        // xóa sản phẩm theo ID xóa hẳn khỏi DB
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        // lấy toàn bộ danh sách sản phẩm có phân trang.
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchByBrand(String brand, Pageable pageable) {
        // tìm kiếm sản phẩm theo tên thương hiệu
        return productRepository.findByBrandContainingIgnoreCase(brand, pageable);
    }

    @Override
    public Page<Product> searchByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable) {
        // tìm kiếm sản phẩm trong khoảng giá min, max
        return productRepository.findByPriceBetween(min, max, pageable);
    }

    @Override
    public Page<Product> searchByStock(Integer stock, Pageable pageable) {
        // tìm sản phẩm có tồn kho stock.
        return productRepository.findByStockGreaterThanEqual(stock, pageable);
    }

    @Override
    public Product findById(Integer id) {
        // tìm sản phẩm theo ID, nếu không có thì báo lỗi.
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

}
