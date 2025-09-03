package edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import edu.model.entity.Admin;
import edu.repo.AdminRepository;
import edu.service.AdminService;

import java.util.Optional;

@Service
@RequiredArgsConstructor // lombok tự động tạo constructor cho các field final
public class AdminServiceImpl implements AdminService {

    // repository để thao tác với bảng admin trong CSDL
    private final AdminRepository adminRepository;

    @Override
    public Admin register(String username, String rawPassword, String confirmPassword) {
        // kiểm tra username đã tồn tại chưa
        if (adminRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Tên người dùng đã tồn tại");
        }
        // kiểm tra xác nhận mật khẩu có khớp không
        if (!rawPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match!");
        }
        // kiểm tra độ mạnh của mật khẩu ít nhất 6 ký tự, có chữ hoa, chữ thường, số, ký tự đặc biệt
        if (!rawPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$")) {
            throw new RuntimeException(
                    "Password must contain at least 6 characters, uppercase, lowercase, number, special char"
            );
        }
        // mã hóa mật khẩu bằng BCrypt
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // tạo đối tượng Admin mới và lưu vào CSDL
        Admin admin = Admin.builder()
                .username(username)
                .password(hashed)
                .build();
        return adminRepository.save(admin);
    }

    @Override
    public Admin login(String username, String rawPassword) {
        // tìm kiếm Admin theo username
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            // kiểm tra mật khẩu nhập vào có khớp với mật khẩu đã mã hóa không
            if (BCrypt.checkpw(rawPassword, admin.getPassword())) {
                return admin;
            }
        }
        // nếu không tìm thấy hoặc sai mật khẩu thì báo lỗi
        throw new RuntimeException("Tên người dùng hoặc mật khẩu không hợp lệ");
    }
}
