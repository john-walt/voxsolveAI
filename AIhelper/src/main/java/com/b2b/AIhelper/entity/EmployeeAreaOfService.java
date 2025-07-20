package com.b2b.AIhelper.entity;

import jakarta.persistence.*;

@Entity
public class EmployeeAreaOfService {

    @Id
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Id
    @ManyToOne
    @JoinColumn(name = "area_id")
    private AreaOfService areaOfService;

    // Getters and Setters
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public AreaOfService getAreaOfService() {
        return areaOfService;
    }

    public void setAreaOfService(AreaOfService areaOfService) {
        this.areaOfService = areaOfService;
    }
}
