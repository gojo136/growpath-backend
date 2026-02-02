package org.example.controller;

import org.example.dto.response.ApiResponse;
import org.example.entity.Note;
import org.example.entity.MediaAttachment;
import org.example.repository.NoteRepository;
import org.example.repository.MediaAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = { "http://localhost:3000", "http://10.0.2.2:3000",
        "http://10.0.2.2:8081" }, allowCredentials = "true")
public class TestController {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private MediaAttachmentRepository mediaAttachmentRepository;

    /**
     * Test endpoint to verify enhanced features
     */
    @GetMapping("/verify-features/{noteId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyEnhancedFeatures(
            @PathVariable("noteId") Long noteId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        try {
            // Get note
            Note note = noteRepository.findByIdAndUserId(noteId, userId)
                    .orElseThrow(() -> new RuntimeException("Note not found"));

            // Get media attachments
            List<MediaAttachment> mediaList = mediaAttachmentRepository.findByNoteIdAndUserId(noteId, userId);

            // Build verification response
            Map<String, Object> verification = new HashMap<>();

            // Basic note data
            verification.put("noteId", note.getId());
            verification.put("title", note.getTitle());
            verification.put("content", note.getContent());
            verification.put("formattedContent", note.getFormattedContent());
            verification.put("checkboxData", note.getCheckboxData());

            // Feature flags
            verification.put("hasImages", note.getHasImages());
            verification.put("hasVoiceNotes", note.getHasVoiceNotes());
            verification.put("hasChecklist", note.getHasChecklist());
            verification.put("hasFormatting", note.getHasFormatting());

            // Media counts
            long imageCount = mediaList.stream().filter(m -> "IMAGE".equals(m.getFileType())).count();
            long voiceCount = mediaList.stream().filter(m -> "VOICE".equals(m.getFileType())).count();

            verification.put("imageCount", imageCount);
            verification.put("voiceCount", voiceCount);
            verification.put("totalMediaFiles", mediaList.size());

            // Media URLs
            verification.put("mediaFiles", mediaList);

            // Checkbox analysis
            if (note.getCheckboxData() != null) {
                String checkboxJson = note.getCheckboxData();
                verification.put("checkboxJsonLength", checkboxJson.length());
                verification.put("hasCheckboxData", !checkboxJson.trim().isEmpty());
            } else {
                verification.put("checkboxJsonLength", 0);
                verification.put("hasCheckboxData", false);
            }

            // Formatting analysis
            if (note.getFormattedContent() != null) {
                String html = note.getFormattedContent();
                verification.put("formattedContentLength", html.length());
                verification.put("hasBoldTags", html.contains("<b>"));
                verification.put("hasItalicTags", html.contains("<i>"));
                verification.put("hasUnderlineTags", html.contains("<u>"));
                verification.put("hasSpanTags", html.contains("<span"));
            } else {
                verification.put("formattedContentLength", 0);
                verification.put("hasBoldTags", false);
                verification.put("hasItalicTags", false);
                verification.put("hasUnderlineTags", false);
                verification.put("hasSpanTags", false);
            }

            return ResponseEntity.ok(ApiResponse.success("Feature verification completed", verification));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint to check system health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> health = new HashMap<>();

        try {
            // Check database connectivity
            long noteCount = noteRepository.count();
            long mediaCount = mediaAttachmentRepository.count();

            health.put("status", "UP");
            health.put("totalNotes", noteCount);
            health.put("totalMediaFiles", mediaCount);
            health.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(ApiResponse.success("System healthy", health));

        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());

            return ResponseEntity.status(500)
                    .body(ApiResponse.error("System unhealthy: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint to validate file uploads
     */
    @GetMapping("/validate-uploads/{noteId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateUploads(
            @PathVariable("noteId") Long noteId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        try {
            List<MediaAttachment> mediaList = mediaAttachmentRepository.findByNoteIdAndUserId(noteId, userId);

            Map<String, Object> validation = new HashMap<>();
            validation.put("totalFiles", mediaList.size());

            for (MediaAttachment media : mediaList) {
                String key = media.getFileType().toLowerCase() + "_" + media.getId();
                Map<String, Object> fileInfo = new HashMap<>();

                fileInfo.put("fileName", media.getFileName());
                fileInfo.put("fileSize", media.getFileSize());
                fileInfo.put("mimeType", media.getMimeType());
                fileInfo.put("filePath", media.getFilePath());
                fileInfo.put("isSupabaseUrl", media.getFilePath().contains("supabase.co"));
                fileInfo.put("isAccessible", media.getFilePath().startsWith("http"));

                validation.put(key, fileInfo);
            }

            return ResponseEntity.ok(ApiResponse.success("Upload validation completed", validation));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed: " + e.getMessage()));
        }
    }
}