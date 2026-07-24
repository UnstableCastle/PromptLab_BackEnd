package com.promptlab.server.service.impl;

import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.entity.User;
import com.promptlab.server.repository.*;
import com.promptlab.server.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

    public UserServiceImpl(UserRepository userRepository, FollowRepository followRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.postRepository = postRepository;
    }

    @Override
    public UserProfileResponse getUserProfile(String username) {
        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean followedByCurrentUser = false;

        if (!currentUsername.equals("anonymousUser")) {
            User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
            if (currentUser != null) {
                followedByCurrentUser = followRepository.existsByFollowerAndFollowing(currentUser, targetUser);
            }
        }

        return new UserProfileResponse(
                targetUser.getUsername(),
                targetUser.getBio(),
                targetUser.getProfilePicture(),
                targetUser.isPrivate(),
                followRepository.countByFollowing(targetUser),
                followRepository.countByFollower(targetUser),
                postRepository.countByUser(targetUser),
                followedByCurrentUser
        );
    }
}