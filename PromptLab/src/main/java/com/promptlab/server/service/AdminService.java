package com.promptlab.server.service;

import com.promptlab.server.dto.AdminDashboardResponse;
import com.promptlab.server.dto.AdminUserResponse;
import com.promptlab.server.dto.PostResponse;

import java.util.List;

public interface AdminService {

    AdminDashboardResponse getDashboardStats();

    List<AdminUserResponse> getUsers();

    List<PostResponse> getRecentPosts();
}