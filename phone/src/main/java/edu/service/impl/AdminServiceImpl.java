package edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import edu.model.entity.Admin;
import edu.repo.AdminRepository;
import edu.service.AdminService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;

    @Override

    public Admin register(String username, String rawPassword, String confirmPassword) {
        if (adminRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        if (!rawPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match!");
        }
        if (!rawPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{6,}$")) {
            throw new RuntimeException(
                    "Password must contain at least 6 characters, uppercase, lowercase, number, special char"
            );
        }
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        Admin admin = Admin.builder()
                .username(username)
                .password(hashed)
                .build();
        return adminRepository.save(admin);
    }

    @Override
    public Admin login(String username, String rawPassword) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (BCrypt.checkpw(rawPassword, admin.getPassword())) {
                return admin;
            }
        }
        throw new RuntimeException("Tên người dùng hoặc mật khẩu không hợp lệ");
    }





}
