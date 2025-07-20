package com.b2b.AIhelper.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ResponseDTO {
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Integer status_code;
    private String message;
    private Map<String, Object> data;

    // Constructor
    public ResponseDTO(Integer status_code, String message, Map<String, Object> data) {
        this.status_code = status_code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public Integer getStatus_code() {
        return status_code;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
