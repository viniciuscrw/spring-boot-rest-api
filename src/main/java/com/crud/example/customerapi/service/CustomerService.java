package com.crud.example.customerapi.service;

import com.crud.example.customerapi.model.Customer;
import com.crud.example.customerapi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@CacheConfig(cacheNames = "customers")
public class CustomerService {

    private final CustomerRepository repository;

    @Autowired
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Cacheable
    public Optional<Customer> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Customer> findByDocument(String document) {
        return repository.findByCpf(document);
    }

    public List<Customer> findAll() {
        return (List<Customer>) repository.findAll();
    }

    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @CachePut(key = "#result.id")
    public Customer update(Customer customer) {
        return repository.save(customer);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
