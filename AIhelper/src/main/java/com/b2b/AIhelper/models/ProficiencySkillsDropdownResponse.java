package com.b2b.AIhelper.models;

import java.util.List;
import java.util.stream.Collectors;

import com.b2b.AIhelper.entity.Employee;
public class ProficiencySkillsDropdownResponse {
    private List<String> employeeNames;
    private List<String> skillNames;
    private List<String> areaNames;

    public ProficiencySkillsDropdownResponse(List<Employee> employees, List<String> skillNames, List<String> areaNames) {
        this.employeeNames = employees.stream()
                                      .map(Employee::getEmployeeName) // Just take employee names
                                      .collect(Collectors.toList());
        this.skillNames = skillNames;
        this.areaNames = areaNames;
    }

    // Getters and Setters
    public List<String> getEmployeeNames() {
        return employeeNames;
    }

    public void setEmployeeNames(List<String> employeeNames) {
        this.employeeNames = employeeNames;
    }

    public List<String> getSkillNames() {
        return skillNames;
    }

    public void setSkillNames(List<String> skillNames) {
        this.skillNames = skillNames;
    }

    public List<String> getAreaNames() {
        return areaNames;
    }

    public void setAreaNames(List<String> areaNames) {
        this.areaNames = areaNames;
    }
}


