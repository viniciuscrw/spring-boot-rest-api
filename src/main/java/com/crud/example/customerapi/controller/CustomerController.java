package com.crud.example.customerapi.controller;

import com.crud.example.customerapi.exception.CustomerNotFoundException;
import com.crud.example.customerapi.model.Address;
import com.crud.example.customerapi.model.Customer;
import com.crud.example.customerapi.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Api(value="Customer CRUD API")
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

    @ApiOperation(value = "View a list of customers", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved customer list or a customer filtered by CPF"),
            @ApiResponse(code = 404, message = "The customer you tried to search for by its CPF was not found")
    })
    @GetMapping
    public ResponseEntity<List<Customer>> findCustomers(
            @ApiParam(value = "Customer CPF which corresponding object will be retrieved") @RequestParam(value = "cpf", required = false) String cpf){
        if (cpf == null) {
            return ResponseEntity.ok(customerService.findAll());
        }

        return customerService.findByDocument(cpf)
                .map(customer -> ResponseEntity.ok(Collections.singletonList(customer)))
                .orElseThrow(() -> new CustomerNotFoundException("Customer with CPF " + cpf + " not found."));
    }

    @ApiOperation(value = "Get a customer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(
            @ApiParam(value = "Customer ID which corresponding object will be retrieved") @PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(String.format(ID_NOT_FOUND, id)));
    }

    @ApiOperation(value = "Add a new customer")
    @PostMapping("/new")
    public ResponseEntity<Customer> create(
            @ApiParam(value = "New customer object to be stored in database") @Valid @RequestBody Customer customer,
            UriComponentsBuilder builder) {
        Address address = customer.getAddress();
        address.setCustomer(customer);
        Customer createdCustomer = customerService.save(customer);
        URI uri = builder.path("/customers/{id}").buildAndExpand(createdCustomer.getId()).toUri();
        return ResponseEntity.created(uri).body(customer);
    }

    @ApiOperation(value = "Update an existing customer")
    @PutMapping("/update/{id}")
    public ResponseEntity<Customer> update(
            @ApiParam(value = "Customer ID to update") @PathVariable Long id,
            @ApiParam(value = "Updated customer object") @Valid @RequestBody Customer updatedCustomer) {
        return customerService.findById(id)
                .map(customer -> {
                    updatedCustomer.setId(id);
                    Address address = updatedCustomer.getAddress();
                    address.setId(customer.getAddress().getId());
                    log.debug("Customer with ID " + id + " updated.");
                    return ResponseEntity.ok(customerService.save(updatedCustomer));
                }).orElseThrow(() -> new CustomerNotFoundException(String.format(ID_NOT_FOUND, id)));
    }

    @ApiOperation(value = "Delete a customer")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(
            @ApiParam(value = "Customer ID from which object will be deleted from database") @PathVariable Long id) {
        return customerService.findById(id)
                .map(customer -> {
                    customerService.deleteById(id);
                    log.debug("Customer with ID " + id + " deleted.");
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new CustomerNotFoundException(String.format(ID_NOT_FOUND, id)));
    }
}
