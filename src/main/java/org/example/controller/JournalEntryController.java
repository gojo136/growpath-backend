package org.example.controller;

import org.example.dto.request.CreateJournalEntryRequest;
import org.example.dto.response.ApiResponse;
import org.example.dto.response.JournalEntryResponse;
import org.example.service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@RestController
@RequestMapping("/api/journals")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<JournalEntryResponse>> createJournalEntry(
            @RequestPart("entry") String entryJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @RequestPart(value = "voiceNote", required = false) MultipartFile voiceNote,
            Authentication authentication) {
        
        try {
            Long userId = (Long) authentication.getPrincipal();
            CreateJournalEntryRequest request = objectMapper.readValue(entryJson, CreateJournalEntryRequest.class);
            
            if (photos != null && photos.size() > 5) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Maximum 5 photos allowed"));
            }
            
            JournalEntryResponse response = journalEntryService.createJournalEntry(userId, request, photos, voiceNote);
            return ResponseEntity.ok(ApiResponse.success("Journal entry created", response));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to create journal entry: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalEntryResponse>>> getUserJournalEntries(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<JournalEntryResponse> entries = journalEntryService.getUserJournalEntries(userId);
            return ResponseEntity.ok(ApiResponse.success("Journal entries retrieved", entries));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve entries: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalEntryResponse>> getJournalEntry(
            @PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            JournalEntryResponse entry = journalEntryService.getJournalEntryById(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Journal entry retrieved", entry));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve entry: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<JournalEntryResponse>> updateJournalEntry(
            @PathVariable Long id,
            @RequestPart("entry") String entryJson,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            @RequestPart(value = "voiceNote", required = false) MultipartFile voiceNote,
            Authentication authentication) {
        
        try {
            Long userId = (Long) authentication.getPrincipal();
            CreateJournalEntryRequest request = objectMapper.readValue(entryJson, CreateJournalEntryRequest.class);
            
            if (photos != null && photos.size() > 5) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Maximum 5 photos allowed"));
            }
            
            JournalEntryResponse response = journalEntryService.updateJournalEntry(id, userId, request, photos, voiceNote);
            return ResponseEntity.ok(ApiResponse.success("Journal entry updated", response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update entry: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJournalEntry(
            @PathVariable Long id, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            journalEntryService.deleteJournalEntry(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Journal entry deleted", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete entry: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<JournalEntryResponse>>> searchJournalEntries(
            @RequestParam("q") String query, Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<JournalEntryResponse> entries = journalEntryService.searchJournalEntries(userId, query);
            return ResponseEntity.ok(ApiResponse.success("Search results", entries));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Search failed: " + e.getMessage()));
        }
    }
}