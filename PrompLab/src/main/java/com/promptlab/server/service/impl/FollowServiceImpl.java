package com.promptlab.server.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.promptlab.server.entity.*;
import com.promptlab.server.repository.*;
import com.promptlab.server.service.FollowService;

@Service
@Transactional
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowServiceImpl(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void followUser(User user, Long userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (user.getId().equals(targetUser.getId())) {
            throw new RuntimeException("You cannot follow yourself.");
        }

        if (followRepository.existsByFollowerAndFollowing(user, targetUser)) {
            throw new RuntimeException("Already following this user.");
        }

        Follow follow = Follow.builder()
                .follower(user)
                .following(targetUser)
                .followStatus(targetUser.isPrivate() ? Follow.FollowStatus.PENDING : Follow.FollowStatus.ACCEPTED)
                .build();

        followRepository.save(follow);
    }

    @Override
    public void unfollowUser(User user, Long userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!followRepository.existsByFollowerAndFollowing(user, targetUser)) {
            throw new RuntimeException("Follow relationship does not exist.");
        }

        followRepository.deleteByFollowerAndFollowing(user, targetUser);
    }
}