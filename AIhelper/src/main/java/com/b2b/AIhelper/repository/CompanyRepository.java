package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.Company;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
