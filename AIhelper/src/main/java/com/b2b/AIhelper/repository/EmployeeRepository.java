package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.Company;
import com.b2b.AIhelper.entity.Employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	List<Employee> findByCompany(Company company);
    // Custom query to find employees by department, or other criteria can be added here
}
