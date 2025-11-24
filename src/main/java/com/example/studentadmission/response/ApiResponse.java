package com.example.studentadmission.response;

import lombok.Data;

@Data
public class ApiResponse {

    private String packageName;
    private String status;
    private String message;
    private Object data;

    public ApiResponse(String packageName, String status, String message, Object data) {
        this.packageName = packageName;
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
