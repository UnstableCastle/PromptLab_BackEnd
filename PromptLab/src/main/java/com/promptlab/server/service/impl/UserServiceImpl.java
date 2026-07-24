package com.promptlab.server.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.dto.UserUpdateRequest;
import com.promptlab.server.entity.User;
import com.promptlab.server.repository.FollowRepository;
import com.promptlab.server.repository.PostRepository;
import com.promptlab.server.repository.UserRepository;
import com.promptlab.server.service.UserService;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;

    // Injected all required repositories
    public UserServiceImpl(UserRepository userRepository, FollowRepository followRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.postRepository = postRepository;
    }

    @Override
    public UserProfileResponse getUserProfileByUsername(String targetUsername, String currentUsername) {
        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + targetUsername));
        return mapToUserProfileResponse(user, currentUsername);
    }

    @Override
    public UserProfileResponse getUserById(Long id, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return mapToUserProfileResponse(user, currentUsername);
    }

    @Override
    public List<UserProfileResponse> getAllUsers(String currentUsername) {
        return userRepository.findAll().stream()
                .map(user -> mapToUserProfileResponse(user, currentUsername))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserProfileResponse updateUser(Long id, UserUpdateRequest request, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Update basic social fields
        if (request.bio() != null) user.setBio(request.bio());
        if (request.profilePicture() != null) user.setProfilePicture(request.profilePicture());
        if (request.isPrivate() != null) user.setPrivate(request.isPrivate());

        userRepository.save(user);
        return mapToUserProfileResponse(user, currentUsername);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String currentUsername) {
        // Implemented the auto-generated stub
        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));
    }

    // --- HELPER METHOD TO MAP TO YOUR EXACT RECORD ---
    private UserProfileResponse mapToUserProfileResponse(User targetUser, String currentUsername) {
        
        // 1. Resolve if the current logged-in user follows this target user
        boolean followedByCurrentUser = false;
        if (currentUsername != null && !currentUsername.equals(targetUser.getUsername())) {
            User currentUser = findByUsername(currentUsername);
            // Reusing the existsByFollowerAndFollowing method from your FollowService setup
            followedByCurrentUser = followRepository.existsByFollowerAndFollowing(currentUser, targetUser);
        }

        // 2. Fetch aggregate statistics using standard Spring Data JPA derived queries
        long followerCount = followRepository.countByFollowing(targetUser);
        long followingCount = followRepository.countByFollower(targetUser);
        long postCount = postRepository.countByUser(targetUser); 

        // 3. Construct and return your specific Record
        return new UserProfileResponse(
            targetUser.getUsername(),
            targetUser.getBio(),
            targetUser.getProfilePicture(),
            targetUser.isPrivate(),
            followerCount,
            followingCount,
            postCount,
            followedByCurrentUser
        );
    }
}