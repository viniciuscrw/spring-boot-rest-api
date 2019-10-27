package com.crud.example.customerapi.repository;

import com.crud.example.customerapi.model.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
}
