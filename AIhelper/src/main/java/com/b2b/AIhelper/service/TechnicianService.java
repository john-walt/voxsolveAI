package com.b2b.AIhelper.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.b2b.AIhelper.entity.Employee;
import com.b2b.AIhelper.repository.EmployeeRepository;
import com.b2b.AIhelper.utils.TechnicianStatus;

import java.util.UUID;

@Service
public class TechnicianService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee updateTechnicianStatus(UUID technicianId, TechnicianStatus status) {
        Employee technician = employeeRepository.findById(technicianId)
            .orElseThrow(() -> new RuntimeException("Technician not found"));
        technician.setStatus(status);
        return employeeRepository.save(technician);
    }
}
