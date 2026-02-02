package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.request.CreateNoteRequest;
import org.example.dto.request.UpdateNoteRequest;
import org.example.dto.request.EnhancedCreateNoteRequest;
import org.example.dto.request.ChecklistItemRequest;
import org.example.dto.response.ApiResponse;
import org.example.dto.response.NoteResponse;
import org.example.dto.response.EnhancedNoteResponse;
import org.example.dto.response.MediaAttachmentResponse;
import org.example.dto.response.ChecklistItemResponse;
import org.example.entity.MediaAttachment;
import org.example.entity.ChecklistItem;
import org.example.repository.MediaAttachmentRepository;
import org.example.repository.ChecklistItemRepository;
import org.example.service.NoteService;
import org.example.service.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = { "http://localhost:3000", "http://10.0.2.2:3000",
        "http://10.0.2.2:8081" }, allowCredentials = "true")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Autowired
    private MediaAttachmentRepository mediaAttachmentRepository;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    /**
     * Create a new note
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> createNote(
            @Valid @RequestBody CreateNoteRequest request,
            Authentication authentication) {

        try {
            Long userId = (Long) authentication.getPrincipal();
            if (userId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid user authentication"));
            }
            
            System.out.println("=================================================");
            System.out.println("🔥🔥🔥 CREATE NOTE REQUEST RECEIVED 🔥🔥🔥");
            System.out.println("🔥 User ID: " + userId);
            System.out.println("🔥 Title: " + request.getTitle());
            System.out.println("🔥 Content Length: " + (request.getContent() != null ? request.getContent().length() : 0));
            System.out.println("🔥 Category: " + request.getCategory());
            System.out.println("=================================================");

            NoteResponse response = noteService.createNote(userId, request);
            System.out.println("✅ Note created successfully with ID: " + response.getId());

            return ResponseEntity.ok(ApiResponse.success("Note created successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error creating note: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to create note"));
        }
    }

    /**
     * Get all user notes with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NoteResponse>>> getUserNotes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        Page<NoteResponse> notes = noteService.getUserNotes(userId, page, size);

        return ResponseEntity.ok(ApiResponse.success("Notes retrieved successfully", notes));
    }

    /**
     * Get a specific note by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponse>> getNoteById(
            @PathVariable("id") Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        NoteResponse note = noteService.getNoteById(userId, id);

        return ResponseEntity.ok(ApiResponse.success("Note retrieved successfully", note));
    }

    /**
     * Update an existing note
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NoteResponse>> updateNote(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateNoteRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        NoteResponse response = noteService.updateNote(userId, id, request);

        return ResponseEntity.ok(ApiResponse.success("Note updated successfully", response));
    }

    /**
     * Archive (soft delete) a note
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> archiveNote(
            @PathVariable("id") Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        noteService.archiveNote(userId, id);

        return ResponseEntity.ok(ApiResponse.success("Note archived successfully", null));
    }

    /**
     * Toggle pin status of a note
     */
    @PatchMapping("/{id}/pin")
    public ResponseEntity<ApiResponse<NoteResponse>> togglePinNote(
            @PathVariable("id") Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        NoteResponse response = noteService.togglePinNote(userId, id);

        return ResponseEntity.ok(ApiResponse.success("Note pin status updated", response));
    }

    /**
     * Search notes by title or content
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<NoteResponse>>> searchNotes(
            @RequestParam(name = "q") String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        Page<NoteResponse> notes = noteService.searchNotes(userId, q, page, size);

        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", notes));
    }

    /**
     * Get notes by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<NoteResponse>>> getNotesByCategory(
            @PathVariable(name = "category") String category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        Page<NoteResponse> notes = noteService.getNotesByCategory(userId, category, page, size);

        return ResponseEntity.ok(ApiResponse.success("Notes by category retrieved successfully", notes));
    }

    /**
     * Create enhanced note with multimedia content
     */
    @PostMapping("/enhanced")
    public ResponseEntity<ApiResponse<EnhancedNoteResponse>> createEnhancedNote(
            @Valid @RequestBody EnhancedCreateNoteRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        System.out.println("🔥 Creating enhanced note for user: " + userId);

        EnhancedNoteResponse response = noteService.createEnhancedNote(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Enhanced note created successfully", response));
    }

    /**
     * Get enhanced note with all related data
     */
    @GetMapping("/enhanced/{id}")
    public ResponseEntity<ApiResponse<EnhancedNoteResponse>> getEnhancedNoteById(
            @PathVariable("id") Long id,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        EnhancedNoteResponse note = noteService.getEnhancedNoteById(userId, id);

        return ResponseEntity.ok(ApiResponse.success("Enhanced note retrieved successfully", note));
    }

    /**
     * Update enhanced note without losing data
     */
    @PutMapping("/enhanced/{id}")
    public ResponseEntity<ApiResponse<EnhancedNoteResponse>> updateEnhancedNote(
            @PathVariable("id") Long id,
            @Valid @RequestBody EnhancedCreateNoteRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        EnhancedNoteResponse response = noteService.updateEnhancedNote(userId, id, request);

        return ResponseEntity.ok(ApiResponse.success("Enhanced note updated successfully", response));
    }

    /**
     * Upload image for a note
     */
    @PostMapping("/upload/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("noteId") Long noteId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        try {
            // Validate that note exists and belongs to user
            noteService.getNoteById(userId, noteId);

            // Save file to Supabase storage
            String fileUrl = supabaseStorageService.saveImageFile(file, userId, noteId);

            // Create media attachment record
            MediaAttachment media = new MediaAttachment(
                    noteId,
                    userId,
                    file.getOriginalFilename(),
                    "IMAGE",
                    fileUrl,
                    file.getSize(),
                    file.getContentType());
            mediaAttachmentRepository.save(media);

            System.out.println("✅ Image uploaded successfully: " + fileUrl);

            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", fileUrl));

        } catch (Exception e) {
            System.err.println("❌ Image upload failed: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to upload image: " + e.getMessage()));
        }
    }

    /**
     * Upload voice note for a note
     */
    @PostMapping("/upload/voice")
    public ResponseEntity<ApiResponse<MediaAttachmentResponse>> uploadVoiceNote(
            @RequestParam("file") MultipartFile file,
            @RequestParam("noteId") Long noteId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        try {
            // Validate that note exists and belongs to user
            noteService.getNoteById(userId, noteId);

            // Save file to Supabase storage
            String fileUrl = supabaseStorageService.saveVoiceFile(file, userId, noteId);

            // Create media attachment record
            MediaAttachment media = new MediaAttachment(
                    noteId,
                    userId,
                    file.getOriginalFilename(),
                    "VOICE",
                    fileUrl,
                    file.getSize(),
                    file.getContentType());
            MediaAttachment savedMedia = mediaAttachmentRepository.save(media);

            System.out.println("✅ Voice note uploaded successfully: " + fileUrl);

            MediaAttachmentResponse response = new MediaAttachmentResponse(savedMedia);
            return ResponseEntity.ok(ApiResponse.success("Voice note uploaded successfully", response));

        } catch (Exception e) {
            System.err.println("❌ Voice upload failed: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to upload voice note: " + e.getMessage()));
        }
    }

    /**
     * Get all media attachments for a note
     */
    @GetMapping("/{noteId}/media")
    public ResponseEntity<ApiResponse<List<MediaAttachmentResponse>>> getNoteMedia(
            @PathVariable("noteId") Long noteId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        try {
            // Verify note ownership
            noteService.getNoteById(userId, noteId);

            // Get all media for this note
            List<MediaAttachment> mediaList = mediaAttachmentRepository.findByNoteIdAndUserId(noteId, userId);
            List<MediaAttachmentResponse> responses = mediaList.stream()
                    .map(MediaAttachmentResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Media retrieved successfully", responses));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve media: " + e.getMessage()));
        }
    }

    /**
     * Delete media attachment
     */
    @DeleteMapping("/media/{mediaId}")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(
            @PathVariable("mediaId") Long mediaId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        try {
            // Get media and verify ownership
            MediaAttachment media = mediaAttachmentRepository.findByIdAndUserId(mediaId, userId)
                    .orElseThrow(() -> new RuntimeException("Media not found or access denied"));

            // Delete file from Supabase storage
            supabaseStorageService.deleteFile(media.getFilePath());

            // Delete database record
            mediaAttachmentRepository.delete(media);

            System.out.println("✅ Media deleted successfully: " + media.getFileName());

            return ResponseEntity.ok(ApiResponse.success("Media deleted successfully", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete media: " + e.getMessage()));
        }
    }

    /**
     * Get total count of user notes
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getUserNotesCount(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Long count = noteService.getUserNotesCount(userId);

        return ResponseEntity.ok(ApiResponse.success("Notes count retrieved successfully", count));
    }

    /**
     * Manage checklist items
     */
    @PostMapping("/{noteId}/checklist")
    public ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> saveChecklistItems(
            @PathVariable("noteId") Long noteId,
            @RequestBody List<ChecklistItemRequest> items,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        List<ChecklistItemResponse> responses = noteService.saveChecklistItems(userId, noteId, items);
        return ResponseEntity.ok(ApiResponse.success("Checklist items saved successfully", responses));
    }

    @GetMapping("/{noteId}/checklist")
    public ResponseEntity<ApiResponse<List<ChecklistItemResponse>>> getChecklistItems(
            @PathVariable("noteId") Long noteId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        List<ChecklistItem> items = checklistItemRepository.findByNoteIdAndUserIdOrderByItemOrderAsc(noteId, userId);
        List<ChecklistItemResponse> responses = items.stream()
                .map(ChecklistItemResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Checklist items retrieved successfully", responses));
    }

    @PutMapping("/checklist/{itemId}")
    public ResponseEntity<ApiResponse<ChecklistItemResponse>> updateChecklistItem(
            @PathVariable("itemId") Long itemId,
            @RequestBody ChecklistItemRequest request,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        ChecklistItemResponse response = noteService.updateChecklistItem(userId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Checklist item updated successfully", response));
    }

    @DeleteMapping("/checklist/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteChecklistItem(
            @PathVariable("itemId") Long itemId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();
        noteService.deleteChecklistItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Checklist item deleted successfully", null));
    }
}