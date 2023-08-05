package com.example.securebusiness.repository;

import com.example.securebusiness.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    Page<Invoice> findAll(Pageable pageable);
}
