package com.promptlab.server.service;

import com.promptlab.server.entity.User;

public interface FollowService {
    void followUser(User user, Long userId);
    void unfollowUser(User user, Long userId);
}