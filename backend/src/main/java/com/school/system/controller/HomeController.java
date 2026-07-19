package com.school.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health & System Info", description = "Root welcome and health monitoring endpoints")
public class HomeController {

    @GetMapping("/")
    @Operation(summary = "Root API Welcome Endpoint")
    public ResponseEntity<Map<String, Object>> rootWelcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Enterprise School Management System API");
        response.put("version", "1.0.0");
        response.put("documentation", "/swagger-ui/index.html");
        response.put("healthCheck", "/actuator/health");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Custom Health Endpoint")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Service is healthy and operational");
        return ResponseEntity.ok(response);
    }
}
