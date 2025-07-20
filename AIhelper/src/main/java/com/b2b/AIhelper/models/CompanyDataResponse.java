package com.b2b.AIhelper.models;

import java.util.List;

public class CompanyDataResponse {
    private List<CompanyDTO> companies;

    public CompanyDataResponse(List<CompanyDTO> companies) {
        this.companies = companies;
    }

    public List<CompanyDTO> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanyDTO> companies) {
        this.companies = companies;
    }
}
