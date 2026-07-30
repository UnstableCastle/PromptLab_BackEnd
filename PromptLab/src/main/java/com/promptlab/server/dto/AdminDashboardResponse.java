package com.promptlab.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {

    private long totalUsers;

    private long totalPosts;

    private long totalLikes;

    private long activeUsers;
}