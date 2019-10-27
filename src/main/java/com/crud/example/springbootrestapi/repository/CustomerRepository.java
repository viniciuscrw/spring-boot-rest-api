package com.crud.example.springbootrestapi.repository;

import com.crud.example.springbootrestapi.model.Customer;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findByCpf(String document);
}
