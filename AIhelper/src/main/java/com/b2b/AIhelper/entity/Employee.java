package com.b2b.AIhelper.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Employee {

    @Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "reporting_manager_id")
    private Employee reportingManager;

    @Column(nullable = false)
    private String employeeName;

    private Boolean isSelf = false; // Self-referencing field
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeSkills> employeeSkills = new HashSet<>(); // Employee-Skills relationship

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeAreaOfService> employeeAreaOfServices = new HashSet<>(); // Employee-AreaOfService relationship
    
    @Column(nullable = false)
    private String skillLevel;  // e.g., "Fresher" or "Experienced"

    // Getters and Setters
    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }


    // Getters and Setters
   

    public Company getCompany() {
        return company;
    }

    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setCompany(Company company) {
        this.company = company;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Employee getReportingManager() {
        return reportingManager;
    }

    public void setReportingManager(Employee reportingManager) {
        this.reportingManager = reportingManager;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Boolean getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(Boolean isSelf) {
        this.isSelf = isSelf;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<EmployeeSkills> getEmployeeSkills() {
        return employeeSkills;
    }

    public void setEmployeeSkills(Set<EmployeeSkills> employeeSkills) {
        this.employeeSkills = employeeSkills;
    }

    public Set<EmployeeAreaOfService> getEmployeeAreaOfServices() {
        return employeeAreaOfServices;
    }

    public void setEmployeeAreaOfServices(Set<EmployeeAreaOfService> employeeAreaOfServices) {
        this.employeeAreaOfServices = employeeAreaOfServices;
    }

    // Helper methods to manage relationships
    public void addSkill(Skill skill) {
        EmployeeSkills employeeSkill = new EmployeeSkills();
        employeeSkill.setEmployee(this);
        employeeSkill.setSkill(skill);
        this.employeeSkills.add(employeeSkill);
    }

    public void removeSkill(Skill skill) {
        this.employeeSkills.removeIf(employeeSkill -> employeeSkill.getSkill().equals(skill));
    }

    public void addAreaOfService(AreaOfService areaOfService) {
        EmployeeAreaOfService employeeArea = new EmployeeAreaOfService();
        employeeArea.setEmployee(this);
        employeeArea.setAreaOfService(areaOfService);
        this.employeeAreaOfServices.add(employeeArea);
    }

    public void removeAreaOfService(AreaOfService areaOfService) {
        this.employeeAreaOfServices.removeIf(employeeArea -> employeeArea.getAreaOfService().equals(areaOfService));
    }
}
