package edu.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Entity                                       // đánh dấu đây là entity JPA
@Table(name = "invoice")                      // ánh xạ tới bảng invoice trong DB
@Getter
@Setter                               // lombok sinh getter setter tự động
@NoArgsConstructor                            // lombok sinh constructor không tham số
@AllArgsConstructor                           // lombok sinh constructor có tham số
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // khóa chính, tự tăng
    private Integer id;

    @ManyToOne
    // nhiều hóa đơn thuộc về 1 khách hàng
    @JoinColumn(name = "customer_id")
    // khóa ngoại trỏ tới bảng customer
    private Customer customer;

    private Date createdAt = new Date();
    // ngày tạo hóa đơn mặc định là thời điểm hiện tại

    private BigDecimal totalAmount;
    // tổng số tiền của hóa đơn

    @Enumerated(EnumType.STRING)
    // lưu enum dưới dạng chuỗi PENDING, CONFIRMED
    private Status status = Status.PENDING;
    // trạng thái hóa đơn mặc định PENDING

    @OneToMany(mappedBy = "invoice",
            // quan hệ 1 nhiều 1 hóa đơn có nhiều chi tiết
            cascade = CascadeType.ALL,
            // thao tác với Invoice tác động luôn tới InvoiceDetail
            orphanRemoval = true)
    // xóa chi tiết khi không còn gắn với Invoice
    private List<InvoiceDetail> details;
    // danh sách chi tiết hóa đơn


    public enum Status {
        PENDING, CONFIRMED, SHIPING, COMPLETED, CANCELED

        // enum Status định nghĩa các trạng thái của hóa đơn
        // PENDING Đang chờ xử lý
        // CONFIRMED Đã xác nhận
        // SHIPING Đang giao hàng
        // COMPLETED Hoàn thành
        // CANCELED Đã hủy
    }
}
