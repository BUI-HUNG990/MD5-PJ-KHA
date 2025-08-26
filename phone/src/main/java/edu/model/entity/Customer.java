package edu.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // Mã KH

    @Column(nullable = false, length = 100)
    private String name;  // Họ tên KH

    @Column(nullable = false, length = 20, unique = true)
    private String phone;  // SĐT

    @Column(length = 100, unique = true)
    private String email;  // Email (cho phép null)

    @Column(length = 255)
    private String address;  // Địa chỉ

    @Column(name = "is_deleted", columnDefinition = "bit(1) default 0")
    private Boolean isDeleted = false;  // true: ẩn, false: hiện
}
