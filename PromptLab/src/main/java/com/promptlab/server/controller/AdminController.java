package com.promptlab.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // Only ROLE_ADMIN can access this endpoint
    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to the Admin Overwatch Dashboard");
    }

    // Add methods here for deleting posts, managing users, etc.
}