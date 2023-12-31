package com.example.securebusiness.repository;

import com.example.securebusiness.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmailIgnoreCase(String email);

    Page<Customer> findAll(Pageable pageable);

    Page<Customer> findByNameContaining(String name, Pageable pageable);
}
