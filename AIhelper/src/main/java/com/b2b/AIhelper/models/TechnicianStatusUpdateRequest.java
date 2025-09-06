package com.b2b.AIhelper.models;

import com.b2b.AIhelper.utils.TechnicianStatus;

public class TechnicianStatusUpdateRequest {
    private TechnicianStatus status;

    public TechnicianStatus getStatus() {
        return status;
    }

    public void setStatus(TechnicianStatus status) {
        this.status = status;
    }
}
