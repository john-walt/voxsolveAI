package com.b2b.AIhelper.models;

import java.util.List;

public class ProficiencySkillsRequest {
    private String employeeName;
    private String shiftFrom;
    private String shiftTo;
    private List<String> skills;  // List of skills such as "Marketing", "Delivery", etc.
    private String areaOfService; // Area of service such as "Location A"
    private String employeeType; // "Fresher" or "Experienced"

    // Getters and Setters
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getShiftFrom() {
        return shiftFrom;
    }

    public void setShiftFrom(String shiftFrom) {
        this.shiftFrom = shiftFrom;
    }

    public String getShiftTo() {
        return shiftTo;
    }

    public void setShiftTo(String shiftTo) {
        this.shiftTo = shiftTo;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getAreaOfService() {
        return areaOfService;
    }

    public void setAreaOfService(String areaOfService) {
        this.areaOfService = areaOfService;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }
}
