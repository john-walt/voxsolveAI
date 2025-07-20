package com.b2b.AIhelper.models;

import java.util.UUID;

public class CompanyDTO {
    private UUID id;
    private String companyName;

    public CompanyDTO(UUID uuid, String companyName) {
        this.id = uuid;
        this.companyName = companyName;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }
}
