package com.example.studentadmission.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseUtil {

    public static ResponseEntity<Map<String, Object>> success(Object data, String message, HttpStatus status) {
        Map<String, Object> map = new LinkedHashMap<>();
        // Note: Package name is hardcoded or fetched dynamically.
        // For static context, we usually don't send package names in APIs,
        // but keeping it per your requirement.
        map.put("package", "com.example.studentadmission");
        map.put("message", message);
        map.put("status", "Success");
        map.put("data", data);
        return new ResponseEntity<>(map, status);
    }

    public static ResponseEntity<Map<String, Object>> error(Object errors, String message, HttpStatus status) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("package", "com.example.studentadmission");
        map.put("message", message);
        map.put("status", "Error");
        if (errors != null) {
            map.put("errors", errors);
        }
        return new ResponseEntity<>(map, status);
    }
}