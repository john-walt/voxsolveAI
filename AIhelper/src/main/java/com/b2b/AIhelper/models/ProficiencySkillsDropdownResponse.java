package com.b2b.AIhelper.models;

import java.util.List;
import java.util.stream.Collectors;

import com.b2b.AIhelper.entity.AreaOfService;
import com.b2b.AIhelper.entity.Employee;
import com.b2b.AIhelper.entity.Skill;
public class ProficiencySkillsDropdownResponse {
    private List<IdNameDTO> employeeTypes;
    private List<IdNameDTO> skillNames;
    private List<IdNameDTO> areaNames;

    public ProficiencySkillsDropdownResponse(List<Employee> employees, List<Skill> skills, List<AreaOfService> areasOfService) {
        this.employeeTypes = employees.stream()
            .map(emp -> new IdNameDTO(
                emp.getRole().getId().toString(),
                emp.getRole().getRoleName()
            ))
            .distinct()
            .collect(Collectors.toList());

        this.skillNames = skills.stream()
            .map(skill -> new IdNameDTO(
                skill.getId().toString(),
                skill.getSkillName()
            ))
            .distinct()
            .collect(Collectors.toList());

        this.areaNames = areasOfService.stream()
            .map(area -> new IdNameDTO(
                area.getId().toString(),
                area.getAreaName()
            ))
            .distinct()
            .collect(Collectors.toList());
    }

	public List<IdNameDTO> getEmployeeTypes() {
		return employeeTypes;
	}

	public void setEmployeeTypes(List<IdNameDTO> employeeTypes) {
		this.employeeTypes = employeeTypes;
	}

	public List<IdNameDTO> getSkillNames() {
		return skillNames;
	}

	public void setSkillNames(List<IdNameDTO> skillNames) {
		this.skillNames = skillNames;
	}

	public List<IdNameDTO> getAreaNames() {
		return areaNames;
	}

	public void setAreaNames(List<IdNameDTO> areaNames) {
		this.areaNames = areaNames;
	}

}


