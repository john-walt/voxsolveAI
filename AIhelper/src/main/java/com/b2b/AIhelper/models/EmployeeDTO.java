package com.b2b.AIhelper.models;

import java.util.UUID;

public class EmployeeDTO {
    private UUID id;
    private String RolesName;

    public EmployeeDTO(UUID uuid, String RolesName) {
        this.id = uuid;
        this.RolesName = RolesName;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getRolesName() {
        return RolesName;
    }
}
