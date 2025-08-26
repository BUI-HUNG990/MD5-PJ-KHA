package edu.service;

import edu.model.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer save(Customer customer);
    void delete(Integer id);
    List<Customer> findAll();
    Customer findById(Integer id);

    void update(Customer customer);
}
