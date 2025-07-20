package com.b2b.AIhelper.controller;

import com.b2b.AIhelper.models.*;
import com.b2b.AIhelper.service.ServiceProviderService;
import com.b2b.AIhelper.utils.RequestStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

//@CrossOrigin(origins = "https://0d97-2406-8800-9014-d2a-b97e-a843-7557-a5e1.ngrok-free.app")
@RestController
@RequestMapping("/service-provider")
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    // Endpoint to get dropdown data for the One-Time Role Setup page
    @Operation(summary = "Get dropdown data for One-Time Role Setup", description = "Returns data for company")
    @CrossOrigin(origins = "*")
    @GetMapping("/role-setup-companynames")
    public ResponseEntity<ResponseDTO> getCompanyNamesInRoleSetup() {
    	CompanyDataResponse data = serviceProviderService.companyNamesInRoleSetup();
    	Map<String, Object> mapData = new HashMap<>();
    	mapData.putAll(new ObjectMapper().convertValue(data, Map.class));
        ResponseDTO responseDTO = new ResponseDTO(200, "Role setup dropdown fetched successfully", mapData);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @Operation(summary = "Get dropdown data for given company ID", description = "Returns departments, roles, and employees based on company")
    @CrossOrigin(origins = "*")
    @GetMapping("/role-setup-dropdowns-from-company")  
    public ResponseEntity<ResponseDTO> getRoleSetupDropDowns(@RequestParam UUID companyId) {
        DropdownDataResponse data = serviceProviderService.getOneTimeRoleSetupDropdowns(companyId);
        Map<String, Object> mapData = new HashMap<>();
    	mapData.putAll(new ObjectMapper().convertValue(data, Map.class));
        ResponseDTO responseDTO = new ResponseDTO(200, "Role setup dropdown fetched successfully", mapData);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // Endpoint to get dropdown data for the Proficiency and Skills page
    @Operation(summary = "Get dropdown data for Proficiency and Skills page", description = "Returns data for employee, skills, and areas of service dropdowns")
    @CrossOrigin(origins = "*")
    @GetMapping("/proficiency-skills-dropdowns")
    public ResponseEntity<ResponseDTO> getProficiencySkillsDropdowns() {
        ProficiencySkillsDropdownResponse data = serviceProviderService.getProficiencySkillsDropdowns();
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("Proficiency skills dropdowns", data);
        ResponseDTO responseDTO = new ResponseDTO(200, "proficiency skills dropdown fetched successfully", mapData);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    // Endpoint to save the One-Time Role Setup data
    @Operation(summary = "Save the role setup data", description = "Saves the selected company, department, role, and reporting manager")
    @CrossOrigin(origins = "*")
    @PostMapping("/role-setup")
    public ResponseEntity<ResponseDTO> saveRoleSetup(@RequestBody RoleSetupRequest request) {
        String result = serviceProviderService.saveRoleSetup(request);        
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("save result", result);
        ResponseDTO responseDTO = new ResponseDTO(200, "role setup saved successfully", mapData);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // Endpoint to save the Proficiency and Skills data
    @Operation(summary = "Save proficiency and skills data", description = "Saves the selected proficiency, skills, and other employee data")
    @CrossOrigin(origins = "*")
    @PostMapping("/proficiency-skills")
    public ResponseEntity<ResponseDTO> saveProficiencySkills(@RequestBody ProficiencySkillsRequest request) {
        String result = serviceProviderService.saveProficiencySkills(request);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("save result", result);
        ResponseDTO responseDTO = new ResponseDTO(200, "proficiency skills saved successfully", mapData);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @Operation(summary = "Fetch service requests by status", description = "Returns service requests filtered by request status")
    @CrossOrigin(origins = "*")
    @GetMapping("/service-requests")
    public ResponseEntity<ResponseDTO> getServiceRequestsByStatus(@RequestParam("status") RequestStatus status) {
        List<ServiceRequestBasicDTO> requests = serviceProviderService.getRequestsByStatus(status);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("requests", requests);
        ResponseDTO responseDTO = new ResponseDTO(200, "Service requests fetched successfully", mapData);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
