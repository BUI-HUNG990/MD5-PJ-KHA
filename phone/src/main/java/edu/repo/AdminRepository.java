package edu.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.model.entity.Admin;

import java.util.Optional;


public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUsername(String username);
}
