package com.example.securebusiness.service.impl;

import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.model.Customer;
import com.example.securebusiness.model.Invoice;
import com.example.securebusiness.model.Stats;
import com.example.securebusiness.repository.CustomerRepository;
import com.example.securebusiness.repository.InvoiceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @InjectMocks

    private CustomerServiceImpl customerService;


    @Test
    void testGetAllCustomers() {
        // Configure CustomerRepository.findAll(...).
        final List<Customer> customers = List.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findAll()).thenReturn(customers);

        // Run the test
        final List<Customer> result = customerService.getAllCustomers();

        Assertions.assertEquals(result, customers);

        // Verify the results
    }

    @Test
    void testGetAllCustomers_CustomerRepositoryReturnsNoItems() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<Customer> result = customerService.getAllCustomers();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGeCustomers() {
        // Configure CustomerRepository.findAll(...).
        final Page<Customer> customers = new PageImpl<>(List.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build()));
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(customers);

        // Run the test
        final Page<Customer> result = customerService.geCustomers(0, 5);

        // Verify the results
    }

    @Test
    void testGeCustomers_CustomerRepositoryReturnsNoItems() {
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<Customer> result = customerService.geCustomers(0, 5);

        // Verify the results
    }

    @Test
    void testFilterCustomers() {
        // Configure CustomerRepository.findByNameContaining(...).
        final Page<Customer> customers = new PageImpl<>(List.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build()));
        when(customerRepository.findByNameContaining(eq("name"), any(Pageable.class))).thenReturn(customers);

        // Run the test
        final Page<Customer> result = customerService.filterCustomers(0, 5, "name");

        // Verify the results
    }

    @Test
    void testFilterCustomers_CustomerRepositoryReturnsNoItems() {
        when(customerRepository.findByNameContaining(eq("name"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<Customer> result = customerService.filterCustomers(0, 5, "name");

        // Verify the results
    }

    @Test
    void testGetCustomer() {
        // Configure CustomerRepository.findById(...).
        final Optional<Customer> customer = Optional.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findById(0L)).thenReturn(customer);

        // Run the test
        final Customer result = customerService.getCustomer(0L);

        // Verify the results
    }

    @Test
    void testGetCustomer_CustomerRepositoryReturnsAbsent() {
        when(customerRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> customerService.getCustomer(0L)).isInstanceOf(ApiException.class);
    }

    @Test
    void testSaveCustomer() {
        final Customer customer = Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build();
        when(customerRepository.existsByEmailIgnoreCase("email")).thenReturn(false);

        // Configure CustomerRepository.save(...).
        final Customer customer1 = Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build();
        when(customerRepository.save(any(Customer.class))).thenReturn(customer1);

        // Run the test
        final Customer result = customerService.saveCustomer(customer);

        // Verify the results
    }

    @Test
    void testSaveCustomer_CustomerRepositoryExistsByEmailIgnoreCaseReturnsTrue() {
        final Customer customer = Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build();
        when(customerRepository.existsByEmailIgnoreCase("email")).thenReturn(true);

        // Run the test
        assertThatThrownBy(() -> customerService.saveCustomer(customer)).isInstanceOf(ApiException.class);
    }

    @Test
    void testSaveCustomer_CustomerRepositorySaveThrowsOptimisticLockingFailureException() {
        final Customer customer = Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build();
        when(customerRepository.existsByEmailIgnoreCase("email")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> customerService.saveCustomer(customer))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testDeleteCustomer() {
        // Configure CustomerRepository.findById(...).
        final Optional<Customer> customer = Optional.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findById(0L)).thenReturn(customer);

        // Run the test
        customerService.deleteCustomer(0L);

        // Verify the results
        verify(customerRepository).delete(any(Customer.class));
    }

    @Test
    void testDeleteCustomer_CustomerRepositoryFindByIdReturnsAbsent() {
        when(customerRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> customerService.deleteCustomer(0L)).isInstanceOf(ApiException.class);
    }

    @Test
    void testDeleteCustomer_CustomerRepositoryDeleteThrowsOptimisticLockingFailureException() {
        // Configure CustomerRepository.findById(...).
        final Optional<Customer> customer = Optional.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findById(0L)).thenReturn(customer);

        doThrow(OptimisticLockingFailureException.class).when(customerRepository).delete(any(Customer.class));

        // Run the test
        assertThatThrownBy(() -> customerService.deleteCustomer(0L))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testUpdateCustomer() {
        final Customer customer = Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build();

        // Configure CustomerRepository.findById(...).
        final Optional<Customer> customer1 = Optional.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findById(0L)).thenReturn(customer1);

        // Configure CustomerRepository.save(...).
        final Customer customer2 = Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build();
        when(customerRepository.save(any(Customer.class))).thenReturn(customer2);

        // Run the test
        final Customer result = customerService.updateCustomer(customer, 0L);

        // Verify the results
    }

    @Test
    void testUpdateCustomer_CustomerRepositoryFindByIdReturnsAbsent() {
        final Customer customer = Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build();
        when(customerRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> customerService.updateCustomer(customer, 0L))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void testUpdateCustomer_CustomerRepositorySaveThrowsOptimisticLockingFailureException() {
        final Customer customer = Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build();

        // Configure CustomerRepository.findById(...).
        final Optional<Customer> customer1 = Optional.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findById(0L)).thenReturn(customer1);

        when(customerRepository.save(any(Customer.class))).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> customerService.updateCustomer(customer, 0L))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testGetStats() {
        // Configure CustomerRepository.findAll(...).
        final List<Customer> customers = List.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findAll()).thenReturn(customers);

        // Configure InvoiceRepository.findAll(...).
        final List<Invoice> invoices = List.of(Invoice.builder()
                .invoiceNumber("invoiceNumber")
                .total(new BigDecimal("0.00"))
                .customer(Customer.builder()
                        .id(0L)
                        .email("email")
                        .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                        .invoices(List.of())
                        .build())
                .build());
        when(invoiceRepository.findAll()).thenReturn(invoices);

        // Run the test
        final Stats result = customerService.getStats();

        // Verify the results
    }

    @Test
    void testGetStats_CustomerRepositoryReturnsNoItems() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        // Configure InvoiceRepository.findAll(...).
        final List<Invoice> invoices = List.of(Invoice.builder()
                .invoiceNumber("invoiceNumber")
                .total(new BigDecimal("0.00"))
                .customer(Customer.builder()
                        .id(0L)
                        .email("email")
                        .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                        .invoices(List.of())
                        .build())
                .build());
        when(invoiceRepository.findAll()).thenReturn(invoices);

        // Run the test
        final Stats result = customerService.getStats();

        // Verify the results
    }

    @Test
    void testGetStats_InvoiceRepositoryReturnsNoItems() {
        // Configure CustomerRepository.findAll(...).
        final List<Customer> customers = List.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findAll()).thenReturn(customers);

        when(invoiceRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final Stats result = customerService.getStats();

        // Verify the results
    }

    @Test
    void testAddInvoiceToCustomer() {
        final Invoice invoice = Invoice.builder()
                .invoiceNumber("invoiceNumber")
                .total(new BigDecimal("0.00"))
                .customer(Customer.builder()
                        .id(0L)
                        .email("email")
                        .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                        .invoices(List.of())
                        .build())
                .build();

        // Configure CustomerRepository.findById(...).
        final Optional<Customer> customer = Optional.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findById(0L)).thenReturn(customer);

        // Run the test
        customerService.addInvoiceToCustomer(invoice, 0L);

        // Verify the results
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void testAddInvoiceToCustomer_CustomerRepositoryReturnsAbsent() {
        final Invoice invoice = Invoice.builder()
                .invoiceNumber("invoiceNumber")
                .total(new BigDecimal("0.00"))
                .customer(Customer.builder()
                        .id(0L)
                        .email("email")
                        .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                        .invoices(List.of())
                        .build())
                .build();
        when(customerRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> customerService.addInvoiceToCustomer(invoice, 0L))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void testAddInvoiceToCustomer_InvoiceRepositoryThrowsOptimisticLockingFailureException() {
        final Invoice invoice = Invoice.builder()
                .invoiceNumber("invoiceNumber")
                .total(new BigDecimal("0.00"))
                .customer(Customer.builder()
                        .id(0L)
                        .email("email")
                        .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                        .invoices(List.of())
                        .build())
                .build();

        // Configure CustomerRepository.findById(...).
        final Optional<Customer> customer = Optional.of(Customer.builder()
                .id(0L)
                .email("email")
                .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                .invoices(List.of(Invoice.builder()
                        .invoiceNumber("invoiceNumber")
                        .total(new BigDecimal("0.00"))
                        .build()))
                .build());
        when(customerRepository.findById(0L)).thenReturn(customer);

        when(invoiceRepository.save(any(Invoice.class))).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> customerService.addInvoiceToCustomer(invoice, 0L))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testGetInvoices1() {
        // Configure InvoiceRepository.findAll(...).
        final List<Invoice> invoices = List.of(Invoice.builder()
                .invoiceNumber("invoiceNumber")
                .total(new BigDecimal("0.00"))
                .customer(Customer.builder()
                        .id(0L)
                        .email("email")
                        .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                        .invoices(List.of())
                        .build())
                .build());
        when(invoiceRepository.findAll()).thenReturn(invoices);

        // Run the test
        final List<Invoice> result = customerService.getInvoices();

        // Verify the results
    }

    @Test
    void testGetInvoices1_InvoiceRepositoryReturnsNoItems() {
        when(invoiceRepository.findAll()).thenReturn(Collections.emptyList());

        // Run the test
        final List<Invoice> result = customerService.getInvoices();

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetInvoices2() {
        // Configure InvoiceRepository.findAll(...).
        final Page<Invoice> invoices = new PageImpl<>(List.of(Invoice.builder()
                .invoiceNumber("invoiceNumber")
                .total(new BigDecimal("0.00"))
                .customer(Customer.builder()
                        .id(0L)
                        .email("email")
                        .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                        .invoices(List.of())
                        .build())
                .build()));
        when(invoiceRepository.findAll(any(Pageable.class))).thenReturn(invoices);

        // Run the test
        final Page<Invoice> result = customerService.getInvoices(0, 5);

        // Verify the results
    }

    @Test
    void testGetInvoices2_InvoiceRepositoryReturnsNoItems() {
        when(invoiceRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<Invoice> result = customerService.getInvoices(0, 5);

        // Verify the results
    }

    @Test
    void testGetInvoice() {
        // Configure InvoiceRepository.findById(...).
        final Optional<Invoice> invoice = Optional.of(Invoice.builder()
                .invoiceNumber("invoiceNumber")
                .total(new BigDecimal("0.00"))
                .customer(Customer.builder()
                        .id(0L)
                        .email("email")
                        .createdAt(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime())
                        .invoices(List.of())
                        .build())
                .build());
        when(invoiceRepository.findById("id")).thenReturn(invoice);

        // Run the test
        final Invoice result = customerService.getInvoice("id");

        // Verify the results
    }

    @Test
    void testGetInvoice_InvoiceRepositoryReturnsAbsent() {
        when(invoiceRepository.findById("id")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> customerService.getInvoice("id")).isInstanceOf(ApiException.class);
    }
}
