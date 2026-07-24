package com.promptlab.server.service;

import java.util.List;
import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.dto.UserUpdateRequest;
import com.promptlab.server.entity.User;

public interface UserService {
    
    UserProfileResponse getUserProfileByUsername(String targetUsername, String currentUsername);
    
    UserProfileResponse getUserById(Long id, String currentUsername);
    
    List<UserProfileResponse> getAllUsers(String currentUsername);
    
    UserProfileResponse updateUser(Long id, UserUpdateRequest request, String currentUsername);
    
    void deleteUser(Long id);

	User findByUsername(String currentUsername);
}