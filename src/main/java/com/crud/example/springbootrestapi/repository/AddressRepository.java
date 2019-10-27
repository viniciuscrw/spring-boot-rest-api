package com.crud.example.springbootrestapi.repository;

import com.crud.example.springbootrestapi.model.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}
