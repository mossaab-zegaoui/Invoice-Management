package com.example.securebusiness.controller;

import com.example.securebusiness.model.Customer;
import com.example.securebusiness.model.HttpResponse;
import com.example.securebusiness.model.Invoice;
import com.example.securebusiness.model.User;
import com.example.securebusiness.service.CustomerService;
import com.example.securebusiness.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/v1/customers")
@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final UserService userService;

    @GetMapping
    public HttpResponse getAllCustomers(@AuthenticationPrincipal User user,
                                        @RequestParam(defaultValue = "0") int pageNo,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        @RequestParam(required = false, defaultValue = "") String name) {
        Page<Customer> customers = customerService.filterCustomers(pageNo, pageSize, name);
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Retrieving List of customers")
                .data(of("user", userService.getUserDtoByEmail(user.getEmail()),
                        "customers", customers,
                        "stats", customerService.getStats()))
                .status(OK)
                .build();
    }

    @GetMapping("{id}")
    public HttpResponse getCustomer(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Retrieving customer with id: " + id)
                .data(of(
                        "user", userService.getUserDtoByEmail(user.getEmail()),
                        "customer", customerService.getCustomer(id)))
                .status(OK)
                .build();
    }

    @PostMapping
    public HttpResponse saveCustomer(@AuthenticationPrincipal User user, @RequestBody @Valid Customer customer) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of(
                        "user", userService.getUserDtoByEmail(user.getEmail()),
                        "customer", customerService.saveCustomer(customer)))
                .message("Creating a customer")
                .status(CREATED)
                .build();
    }

    @PutMapping("{id}")
    public HttpResponse updateCustomer(@AuthenticationPrincipal User user, @RequestBody @Valid Customer customer, @PathVariable Long id) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of(
                        "user", userService.getUserDtoByEmail(user.getEmail()),
                        "customer", customerService.updateCustomer(customer, id)))
                .message("Updating a customer")
                .status(OK)
                .build();
    }

    @DeleteMapping("{id}")
    public HttpResponse delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        customerService.deleteCustomer(id);
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.getUserDtoByEmail(user.getEmail())))
                .message("deleting customer with id: " + id)
                .status(OK)
                .build();
    }

    @GetMapping("invoices")
    public HttpResponse getInvoices(@AuthenticationPrincipal User user,
                                    @RequestParam(defaultValue = "0") int pageNo,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of(
                        "invoices", customerService.getInvoices(pageNo, pageSize),
                        "user", userService.getUserDtoByEmail(user.getEmail())))
                .message("invoices retrieved")
                .status(OK)
                .build();
    }

    @GetMapping("invoices/new")
    public HttpResponse newInvoice(@AuthenticationPrincipal User user) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("customers", customerService.getAllCustomers(),
                        "user", userService.getUserDtoByEmail(user.getEmail())))
                .message("customers retrieved")
                .status(OK)
                .build();
    }


    @PostMapping("invoices/addtoCustomer/{id}")
    public HttpResponse addInvoiceToCustomer(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody Invoice invoice) {
        customerService.addInvoiceToCustomer(invoice, id);
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("customers", customerService.getAllCustomers(),
                        "user", userService.getUserDtoByEmail(user.getEmail())))
                .message("invoice added to customer with id" + id)
                .status(OK)
                .build();
    }

    @GetMapping("invoices/{id}")
    public HttpResponse getInvoice(@AuthenticationPrincipal User user, @PathVariable String id) {
        Invoice invoice = customerService.getInvoice(id);
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.getUserDtoByEmail(user.getEmail()),
                        "customer", invoice.getCustomer(),
                        "invoice", invoice
                ))
                .message("invoice " + id + " retrieved")
                .status(OK)
                .build();
    }

}
