package com.promptlab.server.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.dto.UserUpdateRequest;
import com.promptlab.server.entity.Role;
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
        return userRepository.findByRole(Role.ROLE_USER).stream() 
                .map(user -> mapToUserProfileResponse(user, currentUsername))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserProfileResponse updateUser(Long id, UserUpdateRequest request, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (request.bio() != null) user.setBio(request.bio());
        if (request.profilePicture() != null) user.setProfilePicture(request.profilePicture());
        if (request.isPrivate() != null) user.setPrivate(request.isPrivate());

        userRepository.save(user);
        return mapToUserProfileResponse(user, currentUsername);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, String currentUsername) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        boolean isSelf = currentUser.getId().equals(id);
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isSelf && !isAdmin) {
            throw new RuntimeException("Unauthorized: You only have permission to delete your own profile.");
        }
        
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(String currentUsername) {
        return userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));
    }

    private UserProfileResponse mapToUserProfileResponse(User targetUser, String currentUsername) {
        boolean followedByCurrentUser = false;
        if (currentUsername != null && !currentUsername.equals(targetUser.getUsername())) {
            User currentUser = findByUsername(currentUsername);
            followedByCurrentUser = followRepository.existsByFollowerAndFollowing(currentUser, targetUser);
        }

        long followerCount = followRepository.countByFollowing(targetUser);
        long followingCount = followRepository.countByFollower(targetUser);
        long postCount = postRepository.countByUser(targetUser); 

        // FIXED: Restored the missing constructor arguments
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

    @Override
    @Transactional
    public UserProfileResponse updateUserWithFile(Long id, String username, String email, String bio, MultipartFile file, String currentUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (username != null && !username.isBlank()) user.setUsername(username.trim());
        if (email != null && !email.isBlank()) user.setEmail(email.trim());
        if (bio != null) user.setBio(bio);

        if (file != null && !file.isEmpty()) {
            try {
                java.nio.file.Path userDir = java.nio.file.Paths.get("uploads", "users", String.valueOf(id));
                java.nio.file.Files.createDirectories(userDir);

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                java.nio.file.Path filePath = userDir.resolve(fileName);
                java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = "/uploads/users/" + id + "/" + fileName;
                user.setProfilePicture(fileUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to store profile picture: " + e.getMessage());
            }
        }

        userRepository.save(user);
        return mapToUserProfileResponse(user, currentUsername);
    }
}