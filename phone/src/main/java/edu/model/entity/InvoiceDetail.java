package edu.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "invoice_details")       // ánh xạ entity tới bảng invoice_details
@Getter
@Setter
@NoArgsConstructor                     // lombok sinh constructor rỗng
@AllArgsConstructor                    // lombok sinh constructor có tham số
@Builder                               // lombok hỗ trợ khởi tạo đối tượng theo mẫu Builder
public class InvoiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // khóa chính tự tăng
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    // nhiều chi tiết thuộc về 1 hóa đơn
    @JoinColumn(name = "invoice_id", nullable = false)
    // khóa ngoại liên kết với bảng invoice
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    // nhiều chi tiết gắn với 1 sản phẩm
    @JoinColumn(name = "product_id", nullable = false)
    // khóa ngoại liên kết với bảng product
    private Product product;

    private Integer quantity;
    // số lượng sản phẩm trong hóa đơn

    @Column(name = "unit_price", precision = 15, scale = 2)
    // giá đơn vị
    private BigDecimal unitPrice;
}
