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

    // Define the directory where you want to save uploaded files
    private final String UPLOAD_DIR = "uploads/posts/";

    public String uploadPostFile(Long postId, User user, MultipartFile file) {
        // 1. Check if the file is empty
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file");
        }

        try {
            // 2. Create the upload directory if it doesn't exist yet
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 3. Generate a unique file name to prevent overwriting existing files
            // E.g., "my_photo.jpg" becomes "123e4567-e89b-12d3-a456-426614174000_my_photo.jpg"
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

            // 4. Construct the full file path
            Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);

            // 5. Transfer the binary data from the request to the physical file on your server
            Files.write(filePath, file.getBytes());

            // 6. Return the path/URL so it can be saved to the Post entity in your database
            // Note: In a real app, you might want to call your PostRepository here 
            // to update the post's imageUrl column.
            return filePath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}