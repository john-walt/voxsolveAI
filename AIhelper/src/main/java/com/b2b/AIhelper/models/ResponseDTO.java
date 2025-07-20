package com.b2b.AIhelper.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response")
public class ResponseDTO {

    @Schema(example = "200", description = "Status code of the response")
    private Integer status_code;

    @Schema(example = "Success", description = "Descriptive message")
    private String message;

    @Schema(description = "Returned data (any object)")
    private Object data;
    public ResponseDTO(Integer status_code, String message, Object data) {
        this.status_code = status_code;
        this.message = message;
        this.data = data;
    }

    // Getters and setters
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
