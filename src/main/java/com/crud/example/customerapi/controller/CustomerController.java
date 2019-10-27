package com.crud.example.customerapi.controller;

import com.crud.example.customerapi.exception.CustomerNotFoundException;
import com.crud.example.customerapi.model.Address;
import com.crud.example.customerapi.model.Customer;
import com.crud.example.customerapi.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {

    private static final String ID_NOT_FOUND = "Customer with ID %d not found.";

    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> findCustomers(@RequestParam(value = "cpf", required = false) String cpf){
        if (cpf == null) {
            return ResponseEntity.ok(customerService.findAll());
        }

        return customerService.findByDocument(cpf)
                .map(customer -> ResponseEntity.ok(Collections.singletonList(customer)))
                .orElseThrow(() -> new CustomerNotFoundException("Customer CPF " + cpf + " not found."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(String.format(ID_NOT_FOUND, id)));
    }

    @PostMapping("/new")
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer customer, UriComponentsBuilder builder) {
        Address address = customer.getAddress();
        address.setCustomer(customer);
        Customer createdCustomer = customerService.save(customer);
        URI uri = builder.path("/customers/{id}").buildAndExpand(createdCustomer.getId()).toUri();
        return ResponseEntity.created(uri).body(customer);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @Valid @RequestBody Customer updatedCustomer) {
        return customerService.findById(id)
                .map(customer -> {
                    updatedCustomer.setId(id);
                    Address address = updatedCustomer.getAddress();
                    address.setId(customer.getAddress().getId());
                    log.debug("Customer with ID " + id + " updated.");
                    return ResponseEntity.ok(customerService.save(updatedCustomer));
                }).orElseThrow(() -> new CustomerNotFoundException(String.format(ID_NOT_FOUND, id)));
    }

    @DeleteMapping(path ={"/{id}"})
    public ResponseEntity delete(@PathVariable Long id) {
        return customerService.findById(id)
                .map(customer -> {
                    customerService.deleteById(id);
                    log.debug("Customer with ID " + id + " deleted.");
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new CustomerNotFoundException(String.format(ID_NOT_FOUND, id)));
    }
}
