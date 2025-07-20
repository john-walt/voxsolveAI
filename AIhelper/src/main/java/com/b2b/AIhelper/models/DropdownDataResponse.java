package com.b2b.AIhelper.models;

import com.b2b.AIhelper.entity.Company;
import com.b2b.AIhelper.entity.Department;
import com.b2b.AIhelper.entity.Role;
import com.b2b.AIhelper.entity.Employee;
import java.util.List;

public class DropdownDataResponse {
    private List<DepartmentDTO> departments;
    private List<RolesDTO> roles;
    private List<EmployeeDTO> employees;
	public List<DepartmentDTO> getDepartments() {
		return departments;
	}
	public void setDepartments(List<DepartmentDTO> departments) {
		this.departments = departments;
	}
	public List<RolesDTO> getRoles() {
		return roles;
	}
	public void setRoles(List<RolesDTO> roles) {
		this.roles = roles;
	}
	public List<EmployeeDTO> getEmployees() {
		return employees;
	}
	public void setEmployees(List<EmployeeDTO> employees) {
		this.employees = employees;
	}
	public DropdownDataResponse(List<DepartmentDTO> departments, List<RolesDTO> roles, List<EmployeeDTO> employees) {
		super();
		this.departments = departments;
		this.roles = roles;
		this.employees = employees;
	}

   
  
}
