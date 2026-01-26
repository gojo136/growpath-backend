package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    // Maximum file sizes
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VOICE_SIZE = 50 * 1024 * 1024; // 50MB

    // Allowed file types
    private static final String[] ALLOWED_IMAGE_TYPES = { "image/jpeg", "image/jpg", "image/png", "image/gif",
            "image/webp", "image/*" };
    private static final String[] ALLOWED_VOICE_TYPES = { "audio/mpeg", "audio/mp3", "audio/wav", "audio/3gpp",
            "audio/amr", "audio/*" };

    /**
     * Initialize storage directory
     */
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Create subdirectories for images and voice
            Files.createDirectories(uploadPath.resolve("images"));
            Files.createDirectories(uploadPath.resolve("voice"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    /**
     * Save uploaded image file
     */
    public String saveImageFile(MultipartFile file, Long userId, Long noteId) throws IOException {
        validateImageFile(file);
        return saveFile(file, "images", userId, noteId);
    }

    /**
     * Save uploaded voice file
     */
    public String saveVoiceFile(MultipartFile file, Long userId, Long noteId) throws IOException {
        validateVoiceFile(file);
        return saveFile(file, "voice", userId, noteId);
    }

    /**
     * Save file to storage
     */
    private String saveFile(MultipartFile file, String subDir, Long userId, Long noteId) throws IOException {
        init(); // Ensure directories exist

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = String.format("%d_%d_%s%s",
                userId, noteId, UUID.randomUUID().toString(), fileExtension);

        // Create full path
        Path uploadPath = Paths.get(uploadDir).resolve(subDir);
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Copy file to storage
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path
        return subDir + "/" + uniqueFilename;
    }

    /**
     * Delete file from storage
     */
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(uploadDir).resolve(filePath);
        Files.deleteIfExists(path);
    }

    /**
     * Get file from storage
     */
    public Path getFile(String filePath) {
        return Paths.get(uploadDir).resolve(filePath);
    }

    /**
     * Validate image file
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image file size exceeds maximum limit of 10MB");
        }

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        // Check by content type or file extension
        boolean isValidType = false;
        if (contentType != null) {
            for (String allowedType : ALLOWED_IMAGE_TYPES) {
                if (allowedType.equals(contentType) || contentType.startsWith("image/")) {
                    isValidType = true;
                    break;
                }
            }
        }
        
        // Fallback: check file extension
        if (!isValidType && fileName != null) {
            String ext = fileName.toLowerCase();
            isValidType = ext.endsWith(".jpg") || ext.endsWith(".jpeg") || 
                         ext.endsWith(".png") || ext.endsWith(".gif") || ext.endsWith(".webp");
        }

        if (!isValidType) {
            throw new IllegalArgumentException("Invalid image file. Content-Type: " + contentType + ", FileName: " + fileName);
        }
    }

    /**
     * Validate voice file
     */
    private void validateVoiceFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_VOICE_SIZE) {
            throw new IllegalArgumentException("Voice file size exceeds maximum limit of 50MB");
        }

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        // Check by content type or file extension
        boolean isValidType = false;
        if (contentType != null) {
            for (String allowedType : ALLOWED_VOICE_TYPES) {
                if (allowedType.equals(contentType) || contentType.startsWith("audio/")) {
                    isValidType = true;
                    break;
                }
            }
        }
        
        // Fallback: check file extension
        if (!isValidType && fileName != null) {
            String ext = fileName.toLowerCase();
            isValidType = ext.endsWith(".mp3") || ext.endsWith(".wav") || 
                         ext.endsWith(".3gp") || ext.endsWith(".amr") || ext.endsWith(".m4a");
        }

        if (!isValidType) {
            throw new IllegalArgumentException("Invalid voice file. Content-Type: " + contentType + ", FileName: " + fileName);
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
}
