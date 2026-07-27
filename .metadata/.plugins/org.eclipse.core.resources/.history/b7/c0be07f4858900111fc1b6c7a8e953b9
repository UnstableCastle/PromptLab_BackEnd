package com.promptlab.server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.promptlab.server.entity.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {

    private final String UPLOAD_DIR = "uploads/posts/";

    // A comprehensive set of coding, scripting, and markup extensions
    private final Set<String> ALLOWED_CODE_EXTENSIONS = Set.of(
        "java", "js", "ts", "html", "css", "scss", "sql", "json", "xml", "yml", "yaml",
        "py", "c", "cpp", "h", "hpp", "cs", "php", "rb", "go", "rs", "swift", "kt",
        "sh", "bat", "ps1", "md"
    );

    public String uploadPostFile(Long postId, User user, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload an empty file");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        // Safely extract the file extension (e.g., "Script.java" -> "java")
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        // 1. Validate Images via MIME Type
        boolean isImage = contentType != null && contentType.startsWith("image/");
        
        // 2. Validate Zips via MIME Type
        boolean isZip = contentType != null && (contentType.equals("application/zip") || contentType.equals("application/x-zip-compressed"));
        
        // 3. Validate Code Files via our HashSet
        boolean isCodeFile = ALLOWED_CODE_EXTENSIONS.contains(extension);

        // 4. Reject anything that doesn't match
        if (!isImage && !isZip && !isCodeFile) {
            throw new RuntimeException("Invalid file type. Only images, zip archives, and source code files are allowed.");
        }

        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);
            Files.write(filePath, file.getBytes());

            return filePath.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}