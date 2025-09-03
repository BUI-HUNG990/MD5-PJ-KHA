package edu.service.impl;

import edu.model.entity.Customer;
import edu.repo.CustomerRepository;
import edu.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor // lombok tự động tạo constructor cho final field
public class CustomerServiceImpl implements CustomerService {

    // repository để thao tác với bảng customer
    private final CustomerRepository customerRepository;

    @Override
    public Customer save(Customer customer) {
        // nếu cột isDeleted chưa có giá trị thì gán mặc định = false
        if (customer.getIsDeleted() == null) {
            customer.setIsDeleted(false);
        }

        // kiểm tra email đã tồn tại trong DB chưa
        if (customer.getEmail() != null && customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email này đã tồn tại!");
        }

        // kiểm tra số điện thoại đã tồn tại trong DB chưa
        if (customer.getPhone() != null && customerRepository.existsByPhone(customer.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã tồn tại!");
        }

        // lưu khách hàng vào DB
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Integer id) {
        // tìm khách hàng theo id, nếu không có thì báo lỗi
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        // xóa hẳn khách hàng khỏi DB không dùng soft delete
        customerRepository.delete(customer);
    }

    @Override
    public List<Customer> findAll() {
        // trả về danh sách khách hàng loại bỏ những khách hàng có isDeleted = true
        return customerRepository.findAll()
                .stream()
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .toList();
    }

    @Override
    public Customer findById(Integer id) {
        // tìm khách hàng theo id nếu không tồn tại thì báo lỗi
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public void update(Customer customer) {
        // tìm khách hàng trong DB theo id để cập nhật
        Customer existing = customerRepository.findById(customer.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // kiểm tra email mới có bị trùng không
        if (customer.getEmail() != null &&
                !customer.getEmail().equals(existing.getEmail()) &&
                customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email này đã tồn tại!");
        }

        // kiểm tra số điện thoại mới có bị trùng không
        if (customer.getPhone() != null &&
                !customer.getPhone().equals(existing.getPhone()) &&
                customerRepository.existsByPhone(customer.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã tồn tại!");
        }

        // cập nhật thông tin khách hàng
        existing.setName(customer.getName());
        existing.setPhone(customer.getPhone());
        existing.setEmail(customer.getEmail());
        existing.setAddress(customer.getAddress());

        // lưu thay đổi vào DB
        customerRepository.save(existing);
    }

    @Override
    public Page<Customer> findPaginated(PageRequest pageRequest) {
        // lấy danh sách khách hàng có phân trang
        return customerRepository.findAll(pageRequest);
    }

    @Override
    public List<Customer> searchByNameOrPhone(String name, String phone) {
        // chưa triển khai hiện tại chỉ trả về danh sách rỗng
        return List.of();
    }

    @Override
    public Page<Customer> searchCustomers(String keyword, int page) {
        // chưa triển khai, hiện tại chỉ trả về null
        return null;
    }

    @Override
    public boolean hasInvoice(Integer customerId) {
        // kiểm tra khách hàng có hóa đơn nào không
        return customerRepository.existsInvoiceByCustomerId(customerId);
    }
}
