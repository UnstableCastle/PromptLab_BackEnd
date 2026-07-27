package com.promptlab.server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.promptlab.server.entity.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    public String uploadPostFile(Long postId, User user, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file");
        }

        try {
            // Get the user ID to create the user-specific folder structure
            Long userId = user.getId();
            
            // Construct the new directory path: uploads/users/{userId}/posts/{postId}/
            String postSpecificDir = "uploads/users/" + userId + "/posts/" + postId + "/";
            File directory = new File(postSpecificDir);
            
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path filePath = Paths.get(postSpecificDir + uniqueFileName);

            Files.write(filePath, file.getBytes());

            // Return the relative URL for the frontend to access
            // Output example: /uploads/users/5/posts/13/randomUUID_filename.jpg
            return "/" + postSpecificDir + uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}