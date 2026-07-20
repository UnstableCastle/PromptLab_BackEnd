package com.promptlab.server.service;

import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.entity.Follow;
import com.promptlab.server.entity.Report;
import com.promptlab.server.entity.User;
import com.promptlab.server.repository.FollowRepository;
import com.promptlab.server.repository.ReportRepository;
import com.promptlab.server.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReportRepository reportRepository;

    public UserService(UserRepository userRepository, FollowRepository followRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.reportRepository = reportRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    public UserProfileResponse getUserProfile(String targetUsername) {
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        // Build a safe DTO without exposing passwords or sensitive data
        return new UserProfileResponse(
                targetUser.getUsername(),
                targetUser.getBio(),
                targetUser.getProfilePicture()
        );
    }

    @Transactional
    public void followUser(String targetUsername) {
        User follower = getCurrentUser();
        User following = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("You cannot follow yourself");
        }

        // Toggle follow logic
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(follower, following);

        if (isFollowing) {
            followRepository.deleteByFollowerAndFollowing(follower, following);
        } else {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            followRepository.save(follow);
        }
    }

    @Transactional
    public void reportUser(String targetUsername, String reason) {
        User reporter = getCurrentUser();
        User reported = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reported);
        report.setReason(reason);
        report.setReportedAt(LocalDateTime.now());
        
        reportRepository.save(report);
    }
}