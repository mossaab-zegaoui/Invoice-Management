package com.example.securebusiness.service;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.model.Customer;
import com.example.securebusiness.model.Invoice;
import com.example.securebusiness.model.Stats;

import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers();

    Customer getCustomer(Long id);

    Customer saveCustomer(Customer customer);

    void deleteCustomer(Long id);

    Customer updateCustomer(Customer customer, Long id);

    Stats getStats();

    void addInvoiceToCustomer(Invoice invoice, Long id);

    List<Invoice> getInvoices();

    Invoice getInvoice(String id);
}
