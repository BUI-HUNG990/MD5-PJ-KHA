package edu.service;

import edu.model.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CustomerService {
    Customer save(Customer customer);
    void delete(Integer id);
    List<Customer> findAll();
    Customer findById(Integer id);

    void update(Customer customer);

    Page<Customer> findPaginated(PageRequest pageRequest);

    List<Customer> searchByNameOrPhone(String name, String phone);

    Page<Customer> searchCustomers(String keyword, int page);

    boolean hasInvoice(Integer customerId);

}