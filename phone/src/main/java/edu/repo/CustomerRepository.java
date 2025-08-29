package edu.repo;

import edu.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);


    @Query("SELECT COUNT(i) > 0 FROM Invoice i WHERE i.customer.id = :customerId")
    boolean existsInvoiceByCustomerId(@Param("customerId") Integer customerId);

}


