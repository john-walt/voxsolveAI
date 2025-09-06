package com.b2b.AIhelper.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.b2b.AIhelper.entity.Employee;
import com.b2b.AIhelper.models.TechnicianStatusUpdateRequest;
import com.b2b.AIhelper.service.TechnicianService;

import java.util.UUID;

@RestController
@RequestMapping("/technicians")
public class TechnicianController {

    @Autowired
    private TechnicianService technicianService;

    @PatchMapping("/{technicianId}/status")
    public ResponseEntity<?> updateTechnicianStatus(
            @PathVariable UUID technicianId,
            @RequestBody TechnicianStatusUpdateRequest request) {

        Employee updated = technicianService.updateTechnicianStatus(technicianId, request.getStatus());
        return ResponseEntity.ok().body("Status updated to " + updated.getStatus());
    }
}