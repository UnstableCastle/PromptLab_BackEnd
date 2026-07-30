package com.promptlab.server.controller;

import com.promptlab.server.dto.AdminDashboardResponse;
import com.promptlab.server.dto.AdminUserResponse;
import com.promptlab.server.dto.PostResponse;
import com.promptlab.server.payload.ApiResponse;
import com.promptlab.server.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Dashboard metrics fetched successfully", adminService.getDashboardStats())
        );
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getUsers() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Admin users list fetched successfully", adminService.getUsers())
        );
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getRecentPosts() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Recent admin posts fetched successfully", adminService.getRecentPosts())
        );
    }
}