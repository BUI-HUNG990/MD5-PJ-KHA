package edu.service.impl;

import edu.model.entity.Customer;
import edu.repo.CustomerRepository;
import edu.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    // ✅ Lưu khách hàng mới, kiểm tra duplicate email & phone
    @Override
    public Customer save(Customer customer) {
        if (customer.getIsDeleted() == null) {
            customer.setIsDeleted(false);
        }

        // Kiểm tra email
        if (customer.getEmail() != null && customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email này đã tồn tại!");
        }

        // Kiểm tra phone
        if (customer.getPhone() != null && customerRepository.existsByPhone(customer.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã tồn tại!");
        }

        return customerRepository.save(customer);
    }

    // ✅ Xóa mềm khách hàng
    @Override
    public void delete(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepository.delete(customer); // xóa hẳn khỏi DB
    }

    // ✅ Lấy tất cả khách hàng chưa xóa
    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll()
                .stream()
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .toList();
    }

    // ✅ Tìm khách hàng theo ID
    @Override
    public Customer findById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    // ✅ Cập nhật khách hàng, kiểm tra duplicate email & phone
    @Override
    public void update(Customer customer) {
        Customer existing = customerRepository.findById(customer.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Nếu email thay đổi và đã tồn tại trong DB -> lỗi
        if (customer.getEmail() != null &&
                !customer.getEmail().equals(existing.getEmail()) &&
                customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email này đã tồn tại!");
        }

        // Nếu phone thay đổi và đã tồn tại trong DB -> lỗi
        if (customer.getPhone() != null &&
                !customer.getPhone().equals(existing.getPhone()) &&
                customerRepository.existsByPhone(customer.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã tồn tại!");
        }

        existing.setName(customer.getName());
        existing.setPhone(customer.getPhone());
        existing.setEmail(customer.getEmail());
        existing.setAddress(customer.getAddress());
        // giữ isDeleted cũ
        customerRepository.save(existing);
    }
}
