package com.b2b.AIhelper.service;

import com.b2b.AIhelper.entity.*;
import com.b2b.AIhelper.repository.*;
import com.b2b.AIhelper.utils.RequestStatus;
import com.b2b.AIhelper.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ServiceProviderService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SkillRepository skillRepository;  // New repository for Skill entity

    @Autowired
    private AreaOfServiceRepository areaOfServiceRepository;  // New repository for AreaOfService entity
    
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    // Method to get dropdown data for the One-Time Role Setup page
    public DropdownDataResponse getOneTimeRoleSetupDropdowns(UUID companyId) {
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new RuntimeException("Company not found for ID: " + companyId);
        }

        Company company = companyOpt.get();
        
        List<Department> departments = departmentRepository.findByCompany(company);
        List<Employee> employees = employeeRepository.findByCompany(company);
        List<Role> roles = roleRepository.findAll(); // Assuming roles are global. Filter if needed.
        List<DepartmentDTO> dtoDeptList = departments.stream()
                .map(c -> new DepartmentDTO(c.getId(), c.getDepartmentName()))
                .toList();
        List<EmployeeDTO> dtoEmpList = employees.stream()
                .map(c -> new EmployeeDTO(c.getId(), c.getEmployeeName()))
                .toList();
        List<RolesDTO> dtoRoleList = roles.stream()
                .map(c -> new RolesDTO(c.getId(), c.getRoleName()))
                .toList();
        return new DropdownDataResponse( dtoDeptList, dtoRoleList, dtoEmpList);
    }
    
    public CompanyDataResponse companyNamesInRoleSetup() {
        List<Company> companies = companyRepository.findAll();
        List<CompanyDTO> dtoList = companies.stream()
            .map(c -> new CompanyDTO(c.getId(), c.getCompanyName()))
            .toList();

        return new CompanyDataResponse(dtoList);
    }



    // Method to get dropdown data for the Proficiency and Skills page
    public ProficiencySkillsDropdownResponse getProficiencySkillsDropdowns() {
        List<Employee> employees = employeeRepository.findAll();
        List<Skill> skills = skillRepository.findAll();  // Fetch skills dynamically
        List<AreaOfService> areasOfService = areaOfServiceRepository.findAll();  // Fetch areas of service dynamically

        // Convert skills and areas of service to a List<String> if needed
        List<String> skillNames = skills.stream().map(Skill::getSkillName).toList();
        List<String> areaNames = areasOfService.stream().map(AreaOfService::getAreaName).toList();

        return new ProficiencySkillsDropdownResponse(employees, skillNames, areaNames);
    }

    // Method to save the One-Time Role Setup data
    public String saveRoleSetup(RoleSetupRequest request) {
        // Create and save company, department, and role
        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        companyRepository.save(company);

        Department department = new Department();
        department.setDepartmentName(request.getDepartmentName());
        department.setCompany(company);
        departmentRepository.save(department);

        Role role = new Role();
        role.setRoleName(request.getRoleName());
        roleRepository.save(role);

        // Create and save the employee
        Employee employee = new Employee();
        employee.setEmployeeName(request.getEmployeeName());
        employee.setCompany(company);
        employee.setDepartment(department);
        employee.setRole(role);

        if (request.getReportingManager() != null && !request.getReportingManager().equals("None")) {
            Employee reportingManager = employeeRepository.findById(Long.valueOf(request.getReportingManager())).orElse(null);
            employee.setReportingManager(reportingManager);
        }

        employeeRepository.save(employee);

        return "Role setup has been successfully saved!";
    }

    // Method to save the Proficiency and Skills data
    public String saveProficiencySkills(ProficiencySkillsRequest request) {
        Employee employee = employeeRepository.findById(Long.valueOf(request.getEmployeeName())).orElse(null);
        
        // Create and save shift timing
        ShiftTiming shiftTiming = new ShiftTiming();
        shiftTiming.setEmployee(employee);
        shiftTiming.setShiftFrom(request.getShiftFrom());
        shiftTiming.setShiftTo(request.getShiftTo());

        // Save employee skills
        for (String skill : request.getSkills()) {
            Skill skillEntity = skillRepository.findBySkillName(skill).orElse(null); // Find skill by name
            if (skillEntity != null) {
                employee.addSkill(skillEntity); // Assuming Employee has a method to add skills
            }
        }

        // Save area of service
        AreaOfService areaOfService = areaOfServiceRepository.findByAreaName(request.getAreaOfService()).orElse(null); // Find area by name
        if (areaOfService != null) {
            employee.addAreaOfService(areaOfService); // Assuming Employee has a method to add area of service
        }

        // Save updated employee
        employeeRepository.save(employee);

        return "Proficiency and skills have been successfully saved!";
    }
    
    public List<ServiceRequestBasicDTO> getRequestsByStatus(RequestStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDateTime"));
        Page<ServiceRequest> requestsPage = serviceRequestRepository.findByStatus(status, pageable);
        return requestsPage.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ServiceRequestBasicDTO convertToDTO(ServiceRequest request) {
    	ServiceRequestBasicDTO dto = new ServiceRequestBasicDTO();
        dto.setIssueSummary(request.getIssueSummary());
        dto.setRequestorName(request.getRequestorName());
        dto.setLocation(request.getLocation());
        dto.setEnglishTranslation(request.getCallInfoEnglish());
        dto.setRequestDateTime(request.getRequestDateTime());
        dto.setStatus(request.getStatus());
        dto.setCallInfoMalayalam(request.getCallInfoMalayalam());
        dto.setAudioUrl(request.getAudioUrl());
        return dto;
    }
}
