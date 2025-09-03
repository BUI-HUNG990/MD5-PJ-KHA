package edu.service;

import edu.model.entity.Admin;

// interface định nghĩa các chức năng nghiệp vụ cho Admin
public interface AdminService {
    Admin register(String username, String rawPassword, String confirmPassword);

    Admin login(String username, String rawPassword);
}
