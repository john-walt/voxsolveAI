package com.b2b.AIhelper.models;

import java.util.UUID;

public class DepartmentDTO {
    private UUID id;
    private String DepartmentName;

    public DepartmentDTO(UUID uuid, String DepartmentName) {
        this.id = uuid;
        this.DepartmentName = DepartmentName;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }
}
