package com.example.securebusiness.service;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.model.Customer;
import com.example.securebusiness.model.Invoice;
import com.example.securebusiness.model.Stats;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Page<Customer> geCustomers(int pageNo, int pageSize);

    Customer getCustomer(Long id);

    Customer saveCustomer(Customer customer);

    void deleteCustomer(Long id);

    Customer updateCustomer(Customer customer, Long id);

    Stats getStats();

    void addInvoiceToCustomer(Invoice invoice, Long id);

    List<Invoice> getInvoices();
    Page<Invoice> getInvoices(int pageNo, int pageSize);

    Invoice getInvoice(String id);

    Page<Customer> filterCustomers(int pageNo, int pageSize, String name);
}
