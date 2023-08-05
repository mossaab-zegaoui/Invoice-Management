package com.example.securebusiness.service.impl;

import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.model.Customer;
import com.example.securebusiness.model.Invoice;
import com.example.securebusiness.model.Stats;
import com.example.securebusiness.repository.CustomerRepository;
import org.apache.commons.lang3.RandomStringUtils;

import com.example.securebusiness.service.CustomerService;
import com.example.securebusiness.repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Page<Customer> geCustomers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Customer> pagedCustomers = customerRepository.findAll(pageable);
        if (pagedCustomers.isEmpty())
            return Page.empty(pageable);
        return pagedCustomers;
    }

    @Override
    public Page<Customer> filterCustomers(int pageNo, int pageSize, String name) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Customer> customers = customerRepository.findByNameContaining(name, pageable);
        if (customers.isEmpty())
            return Page.empty(pageable);
        return customers;
    }

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("customer with id: " + id + " not found"));
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        if (customerRepository.existsByEmailIgnoreCase(customer.getEmail()))
            throw new ApiException("email is already taken");
        customer.setCreatedAt(new Date());
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("customer " + id + " not found"));
        customerRepository.delete(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer, Long id) {
        Customer savedCustomer = customerRepository.findById(id).orElseThrow(() -> new ApiException("customer with id" + id + " not found"));
        customer.setId(id);
        customer.setInvoices(savedCustomer.getInvoices());
        return customerRepository.save(customer);
    }

    @Override
    public Stats getStats() {
        int totalCustomers = customerRepository.findAll().size();
        int totalInvoices = invoiceRepository.findAll().size();
        BigDecimal totalBill = invoiceRepository.findAll()
                .stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Stats(totalCustomers, totalInvoices, totalBill);
    }

    @Override
    public void addInvoiceToCustomer(Invoice invoice, Long id) {
        invoice.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("customer with id" + id + " not found"));
        invoice.setCustomer(customer);
        invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Page<Invoice> getInvoices(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Invoice> invoices = invoiceRepository.findAll(pageable);
        if (invoices.isEmpty())
            return Page.empty(pageable);
        return invoices;
    }

    @Override
    public Invoice getInvoice(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ApiException("invoice wit id: " + id + " not found"));
    }


}
