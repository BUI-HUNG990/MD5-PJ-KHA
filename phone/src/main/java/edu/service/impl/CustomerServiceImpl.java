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
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer save(Customer customer) {
        if (customer.getIsDeleted() == null) {
            customer.setIsDeleted(false);
        }

        if (customer.getEmail() != null && customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email này đã tồn tại!");
        }


        if (customer.getPhone() != null && customerRepository.existsByPhone(customer.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã tồn tại!");
        }

        return customerRepository.save(customer);
    }


    @Override
    public void delete(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepository.delete(customer); // xóa hẳn khỏi DB
    }


    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll()
                .stream()
                .filter(c -> !Boolean.TRUE.equals(c.getIsDeleted()))
                .toList();
    }


    @Override
    public Customer findById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }


    @Override
    public void update(Customer customer) {
        Customer existing = customerRepository.findById(customer.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));


        if (customer.getEmail() != null &&
                !customer.getEmail().equals(existing.getEmail()) &&
                customerRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email này đã tồn tại!");
        }


        if (customer.getPhone() != null &&
                !customer.getPhone().equals(existing.getPhone()) &&
                customerRepository.existsByPhone(customer.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã tồn tại!");
        }

        existing.setName(customer.getName());
        existing.setPhone(customer.getPhone());
        existing.setEmail(customer.getEmail());
        existing.setAddress(customer.getAddress());

        customerRepository.save(existing);
    }

    @Override
    public Page<Customer> findPaginated(PageRequest pageRequest) {
        return customerRepository.findAll(pageRequest);
    }

    @Override
    public List<Customer> searchByNameOrPhone(String name, String phone) {
        return List.of();
    }

    @Override
    public Page<Customer> searchCustomers(String keyword, int page) {
        return null;
    }

}
