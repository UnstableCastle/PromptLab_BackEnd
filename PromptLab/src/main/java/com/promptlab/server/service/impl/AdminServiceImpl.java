package com.promptlab.server.service.impl;

import com.promptlab.server.dto.AdminDashboardResponse;
import com.promptlab.server.dto.AdminUserResponse;
import com.promptlab.server.dto.PostResponse;
import com.promptlab.server.repository.PostRepository;
import com.promptlab.server.repository.UserRepository;
import com.promptlab.server.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardStats() {
        
        long totalUsers = userRepository.count();
        long totalPosts = postRepository.count();
        long totalLikes = postRepository.countTotalLikes();
        
        // Active users logic can be dynamically implemented later if session tracking is added
        long activeUsers = totalUsers; 

        return new AdminDashboardResponse(
                totalUsers,
                totalPosts,
                totalLikes,
                activeUsers
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getUsers() {
        return userRepository.fetchAdminUserSummaries();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getRecentPosts() {
        return postRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                // Pass 'false' for the hasUpvoted parameter since this is an admin view
                .map(post -> PostResponse.fromEntity(post, false)) 
                .toList();
    }
}