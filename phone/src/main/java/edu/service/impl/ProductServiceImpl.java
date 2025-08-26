package edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import edu.model.entity.Product;
import edu.repo.ProductRepository;
import edu.service.ProductService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Product save(Product product) {
        if (product.getImage() == null) {
            product.setImage(""); // hoặc "no-image.png"
        }
        return productRepository.save(product);
    }

    @Override
    public Product update(Product product) {
        Product existing = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(product.getName());
        existing.setBrand(product.getBrand());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        return productRepository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrandContainingIgnoreCase(brand, pageable);
    }

    @Override
    public Page<Product> searchByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable) {
        return productRepository.findByPriceBetween(min, max, pageable);
    }

    @Override
    public Page<Product> searchByStock(Integer stock, Pageable pageable) {
        return productRepository.findByStockGreaterThanEqual(stock, pageable);
    }

    @Override
    public Product findById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

}
