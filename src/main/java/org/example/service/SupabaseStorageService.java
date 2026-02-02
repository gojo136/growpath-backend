package org.example.service;

import org.example.config.SupabaseStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import okhttp3.*;
import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Autowired
    private SupabaseStorageConfig storageConfig;

    private final OkHttpClient httpClient = new OkHttpClient();

    public String uploadFile(MultipartFile file, String bucket, Long userId) throws IOException {
        if (bucket.equals("journal-photos")) {
            validateImageFile(file);
            return uploadToSupabase(file, storageConfig.getImagesBucket(), generateFileName("journal", userId, 0L, getFileExtension(file.getOriginalFilename())));
        } else if (bucket.equals("journal-voice")) {
            validateAudioFile(file);
            return uploadToSupabase(file, storageConfig.getAudioBucket(), generateFileName("journal_voice", userId, 0L, getFileExtension(file.getOriginalFilename())));
        } else {
            throw new IOException("Unsupported bucket: " + bucket);
        }
    }

    public String saveImageFile(MultipartFile file, Long userId, Long noteId) throws IOException {
        validateImageFile(file);
        String fileName = generateFileName("img", userId, noteId, getFileExtension(file.getOriginalFilename()));
        return uploadToSupabase(file, storageConfig.getImagesBucket(), fileName);
    }

    public String saveVoiceFile(MultipartFile file, Long userId, Long noteId) throws IOException {
        validateAudioFile(file);
        String fileName = generateFileName("audio", userId, noteId, getFileExtension(file.getOriginalFilename()));
        return uploadToSupabase(file, storageConfig.getAudioBucket(), fileName);
    }

    public String saveVoiceNoteFile(MultipartFile file, Long userId) throws IOException {
        validateAudioFile(file);
        String fileName = generateFileName("voice", userId, 0L, getFileExtension(file.getOriginalFilename()));
        return uploadToSupabase(file, storageConfig.getVoiceBucket(), fileName);
    }

    private String uploadToSupabase(MultipartFile file, String bucket, String fileName) throws IOException {
        String uploadUrl = storageConfig.getStorageUrl() + "/object/" + bucket + "/" + fileName;

        RequestBody requestBody = RequestBody.create(
                file.getBytes(),
                MediaType.parse(file.getContentType()));

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + storageConfig.getSupabaseServiceKey())
                .addHeader("Content-Type", file.getContentType())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Upload failed: " + response.code());
            }
            return storageConfig.getPublicUrl(bucket, fileName);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            String bucket = extractBucketFromUrl(fileUrl);

            String deleteUrl = storageConfig.getStorageUrl() + "/object/" + bucket + "/" + fileName;

            Request request = new Request.Builder()
                    .url(deleteUrl)
                    .delete()
                    .addHeader("Authorization", "Bearer " + storageConfig.getSupabaseServiceKey())
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Failed to delete file: " + response.code());
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
    }

    private void validateImageFile(MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw new IOException("File is empty");
        if (file.getSize() > 5 * 1024 * 1024)
            throw new IOException("Image too large (max 5MB)");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Invalid image format");
        }
    }

    private void validateAudioFile(MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw new IOException("File is empty");
        if (file.getSize() > 10 * 1024 * 1024)
            throw new IOException("Audio too large (max 10MB)");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new IOException("Invalid audio format");
        }
    }

    private String generateFileName(String prefix, Long userId, Long noteId, String extension) {
        return String.format("%s_%d_%d_%s.%s", prefix, userId, noteId, UUID.randomUUID().toString().substring(0, 8),
                extension);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains("."))
            return "bin";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private String extractBucketFromUrl(String url) {
        if (url.contains("/" + storageConfig.getImagesBucket() + "/"))
            return storageConfig.getImagesBucket();
        if (url.contains("/" + storageConfig.getAudioBucket() + "/"))
            return storageConfig.getAudioBucket();
        if (url.contains("/" + storageConfig.getVoiceBucket() + "/"))
            return storageConfig.getVoiceBucket();
        return "default";
    }
}