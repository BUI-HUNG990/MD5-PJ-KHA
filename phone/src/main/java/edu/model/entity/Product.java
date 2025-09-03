package edu.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "product")                     // entity ánh xạ đến bảng product
@Getter
@Setter
@AllArgsConstructor                          // lombok tạo constructor đầy đủ tham số
@NoArgsConstructor                           // lombok tạo constructor rỗng
@Builder                                     // lombok hỗ trợ khởi tạo theo mẫu Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // khóa chính tự động tăng
    private Integer id;

    @Column(nullable = false, length = 100)
    // tên sản phẩm, không được null, tối đa 100 ký tự
    private String name;

    @Column(nullable = false, length = 50)
    // thương hiệu sản phẩm, không được null tối đa 50 ký tự
    private String brand;

    @Column(nullable = false, precision = 12, scale = 2)
    // giá sản phẩm 12 số với 2 số thập phân
    private BigDecimal price;

    @Column(nullable = false)
    // số lượng tồn kho, không được null
    private Integer stock;

    @Column(length = 255, nullable = true)
    // đường dẫn ảnh sản phẩm
    private String image = "";

    @Column(name = "is_deleted", columnDefinition = "bit(1) default 0")
    // trạng thái xóa mềm
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "product",
            // một sản phẩm có thể nằm trong nhiều chi tiết hóa đơn
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<InvoiceDetail> invoiceDetails;
}
