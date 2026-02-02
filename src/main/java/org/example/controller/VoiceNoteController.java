package org.example.controller;

import org.example.dto.response.ApiResponse;
import org.example.dto.response.VoiceNoteResponse;
import org.example.service.VoiceNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/voice-notes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VoiceNoteController {

    @Autowired
    private VoiceNoteService voiceNoteService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<VoiceNoteResponse>> createVoiceNote(
            @RequestPart("title") String title,
            @RequestPart("file") MultipartFile file,
            @RequestPart("durationMs") String durationMs,
            @RequestPart(value = "category", required = false) String category,
            Authentication authentication) {
        
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            // Handle potential quotes or extra spaces in multipart string parts
            String cleanedDuration = durationMs.replace("\"", "").trim();
            Long duration = Long.parseLong(cleanedDuration);
            
            String cleanedTitle = title.replace("\"", "").trim();
            String cat = (category != null) ? category.replace("\"", "").trim() : "All";
            
            VoiceNoteResponse response = voiceNoteService.createVoiceNote(userId, cleanedTitle, file, duration, cat);
            return ResponseEntity.ok(ApiResponse.success("Voice note created successfully", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to create voice note: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VoiceNoteResponse>>> getUserVoiceNotes(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<VoiceNoteResponse> response = voiceNoteService.getVoiceNotesByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Voice notes retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve voice notes: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VoiceNoteResponse>>> searchVoiceNotes(
            @RequestParam("q") String query,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<VoiceNoteResponse> response = voiceNoteService.searchVoiceNotes(userId, query);
            return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<VoiceNoteResponse>> updateVoiceNote(
            @PathVariable("id") Long id,
            @RequestParam(value = "title", required = false) String titleParam,
            @RequestPart(value = "title", required = false) String titlePart,
            Authentication authentication) {
        try {
            System.out.println("Received update request for voice note ID: " + id);
            
            String title = (titleParam != null) ? titleParam : titlePart;
            System.out.println("New title: " + title);
            
            if (title == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Title is required"));
            }
            
            Long userId = (Long) authentication.getPrincipal();
            String cleanedTitle = title.replace("\"", "").trim();
            
            System.out.println("Cleaned title: " + cleanedTitle);
            System.out.println("User ID: " + userId);
            
            VoiceNoteResponse response = voiceNoteService.updateVoiceNote(id, userId, cleanedTitle);
            return ResponseEntity.ok(ApiResponse.success("Voice note renamed successfully", response));
        } catch (RuntimeException e) {
            System.err.println("Runtime error in updateVoiceNote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to rename voice note: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Unexpected error in updateVoiceNote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVoiceNote(
            @PathVariable("id") Long id,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            voiceNoteService.deleteVoiceNote(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Voice note deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete voice note: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<ApiResponse<VoiceNoteResponse>> updateVoiceNoteTitle(
            @PathVariable("id") Long id,
            @RequestBody String newTitle,
            Authentication authentication) {
        
        System.out.println("=== SIMPLE UPDATE VOICE NOTE TITLE ===");
        System.out.println("ID: " + id + ", Raw title: '" + newTitle + "'");
        
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            // Remove quotes and clean the title
            String cleanedTitle = newTitle;
            if (cleanedTitle.startsWith("\"") && cleanedTitle.endsWith("\"")) {
                cleanedTitle = cleanedTitle.substring(1, cleanedTitle.length() - 1);
            }
            cleanedTitle = cleanedTitle.trim();
            
            System.out.println("Cleaned title: '" + cleanedTitle + "'");
            
            VoiceNoteResponse response = voiceNoteService.updateVoiceNote(id, userId, cleanedTitle);
            return ResponseEntity.ok(ApiResponse.success("Updated", response));
        } catch (Exception e) {
            System.err.println("Simple update error: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
