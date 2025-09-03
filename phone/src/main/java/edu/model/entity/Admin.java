package edu.model.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity                       // đánh dấu đây là một entity của JPA ánh xạ tới bảng trong DB
@Table(name = "admin")        // liên kết entity này với bảng admin trong csdl
@Getter                       // lombok tự động sinh getter cho các trường
@Setter                       // lombok tự động sinh setter cho các trường
@AllArgsConstructor           // lombok tự động tạo constructor với tất cả tham số
@NoArgsConstructor            // lombok tự động tạo constructor rỗng
@Builder                      // lombok hỗ trợ xây dựng đối tượng theo mẫu Builder
public class Admin {

    @Id
    // khóa chính của bảng
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // tự động tăng id trong csdl
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    // cột username bắt buộc không trùng lặp tối đa 50 ký tự
    private String username;

    @Column(nullable = false, length = 255)
    // cột password bắt buộc tối đa 255 ký tự
    private String password;
}
