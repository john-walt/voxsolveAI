package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // Custom method to find by either phoneNumber or whatsappNumber
    Optional<Customer> findByPhoneNumberOrWhatsappNumber(String phoneNumber, String whatsappNumber);
}
