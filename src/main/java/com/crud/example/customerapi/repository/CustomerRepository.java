package com.crud.example.customerapi.repository;

import com.crud.example.customerapi.model.Customer;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findByCpf(String document);
}
