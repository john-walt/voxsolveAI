package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.Company;
import com.b2b.AIhelper.entity.Department;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

	List<Department> findByCompany(Company company);
    // You can add custom queries here if needed (e.g., find by company)
}
