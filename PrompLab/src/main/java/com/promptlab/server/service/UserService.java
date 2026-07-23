package com.promptlab.server.service;

import com.promptlab.server.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getUserProfile(String username);
}