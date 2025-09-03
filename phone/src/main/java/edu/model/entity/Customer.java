package edu.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity                                  // đánh dấu đây là entity JPA
@Table(name = "customer")                // ánh xạ tới bảng customer trong DB
@Getter                                  // lombok sinh getter tự động
@Setter                                  // lombok sinh setter tự động
@AllArgsConstructor                      // lombok sinh constructor có tham số
@NoArgsConstructor                       // lombok sinh constructor rỗng
@Builder                                 // lombok hỗ trợ tạo đối tượng theo mẫu Builder
public class Customer {

    @Id
    // khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // tự động tăng (auto increment)
    private Integer id;

    @Column(nullable = false, length = 100)
    // cột name bắt buộc tối đa 100 ký tự
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    // cột phone bắt buộc tối đa 20 ký tự không trùng
    private String phone;

    @Column(length = 100, unique = true)
    // cột email tối đa 100 ký tự không trùng
    private String email;

    @Column(length = 255)
    // cột address tối đa 255 ký tự
    private String address;

    @Column(name = "is_deleted", columnDefinition = "bit(1) default 0")
    private Boolean isDeleted = false;
    // cột is_deleted đánh dấu đã xóa mềm

    @OneToMany(mappedBy = "customer",
            // liên kết 1 nhiều với Invoice 1 khách hàng có nhiều hóa đơn
            cascade = CascadeType.ALL,
            // khi thao tác với customer sẽ áp dụng luôn cho Invoice thêm xóa
            orphanRemoval = true)
    // xóa hóa đơn nếu không còn gắn với Customer
    private List<Invoice> invoices;
    // danh sách hóa đơn của khách hàng
}
