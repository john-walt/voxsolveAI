package com.b2b.AIhelper.repository;

import com.b2b.AIhelper.entity.Company;
import com.b2b.AIhelper.entity.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

	List<Employee> findByCompany(Company company);
	
	Optional<Employee> findByEmployeeName(String employeeName);

    // Custom query to find employees by department, or other criteria can be added here
}
