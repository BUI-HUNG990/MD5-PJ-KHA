package edu.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.model.entity.Admin;
import edu.service.AdminService;

@Controller
@RequiredArgsConstructor
// lombok sẽ tự động tạo constructor cho các field final
public class AdminController {
    private final AdminService adminService;
    // service xử lý logic cho Admin

    @GetMapping("/")
    public String home() {
        // tự động chuyển hướng về login
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        // hiển thị trang login
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            // gọi service để kiểm tra đăng nhập
            Admin admin = adminService.login(username, password);

            // lưu thành công, lưu thông tin admin vào session
            session.setAttribute("loggedAdmin", admin);

            // chuyển hướng sang trang dashboard
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            // đăng nhập thất bại, trả về login với thông báo lỗi
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        // hiển thị trang đăng ký
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {
        try {
            // kiểm tra xác nhận mật khẩu
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Mật khẩu không khớp, nhập lại!");
                return "register";
            }

            // gọi service để đăng ký admin mới
            adminService.register(username, password, confirmPassword);

            // sau khi đăng ký thành công thì chuyển hướng về trang login
            return "redirect:/login";
        } catch (RuntimeException e) {
            // có lỗi thì hiện lại trang đăng ký cùng thông báo
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // lấy thông tin admin từ session
        Admin admin = (Admin) session.getAttribute("loggedAdmin");

        // nếu chưa đăng nhập thì chuyển hướng về login
        if (admin == null) {
            return "redirect:/login";
        }

        // truyền thông tin admin sang trang dashboard
        model.addAttribute("admin", admin);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // xóa session để đăng xuất
        session.invalidate();

        // chuyển hướng về trang login
        return "redirect:/login";
    }
}
