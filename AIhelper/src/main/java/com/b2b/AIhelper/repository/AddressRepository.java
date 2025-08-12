package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.Address;
import com.b2b.AIhelper.entity.Customer;
import com.b2b.AIhelper.entity.ServiceRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByPin(String pin);
}

