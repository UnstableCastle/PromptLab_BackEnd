package com.promptlab.server.service;

import com.promptlab.server.entity.User;

public interface UpvoteService {
    void toggleUpvote(Long postId, User user);
}