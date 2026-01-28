package org.example.service;

import org.example.dto.request.CreateNoteRequest;
import org.example.dto.request.UpdateNoteRequest;
import org.example.dto.request.EnhancedCreateNoteRequest;
import org.example.dto.request.ChecklistItemRequest;
import org.example.dto.response.NoteResponse;
import org.example.dto.response.EnhancedNoteResponse;
import org.example.dto.response.ChecklistItemResponse;
import org.example.dto.CheckboxDataDto;
import org.example.entity.Note;
import org.example.entity.ChecklistItem;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.NoteRepository;
import org.example.repository.ChecklistItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteService {

    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create a new note for the user
     */
    public NoteResponse createNote(Long userId, CreateNoteRequest request) {
        logger.info("Creating note for user: {}", userId);

        // Validate inputs
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content is required");
        }

        Note note = new Note(
                userId,
                request.getTitle() != null ? request.getTitle().trim() : null,
                request.getContent().trim(),
                request.getCategory() != null ? request.getCategory().trim() : null
        );
        
        // Ensure checkboxData is null for regular notes
        note.setCheckboxData(null);

        Note savedNote = noteRepository.save(note);
        logger.info("Note created successfully with ID: {}", savedNote.getId());

        return NoteResponse.from(savedNote);
    }

    /**
     * Get all active notes for a user with pagination
     */
    public Page<NoteResponse> getUserNotes(Long userId, int page, int size) {
        logger.info("Fetching notes for user: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Note> notes = noteRepository.findActiveNotesByUserId(userId, pageable);

        return notes.map(NoteResponse::from);
    }

    /**
     * Get a specific note by ID (with user validation)
     */
    public NoteResponse getNoteById(Long userId, Long noteId) {
        logger.info("Fetching note {} for user: {}", noteId, userId);

        Note note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + noteId));

        return NoteResponse.from(note);
    }

    /**
     * Update an existing note
     */
    public NoteResponse updateNote(Long userId, Long noteId, UpdateNoteRequest request) {
        logger.info("Updating note {} for user: {}", noteId, userId);

        Note note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + noteId));

        // Update fields
        note.setTitle(request.getTitle() != null ? request.getTitle().trim() : null);
        note.setContent(request.getContent().trim());
        note.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);

        Note updatedNote = noteRepository.save(note);
        logger.info("Note updated successfully: {}", noteId);

        return NoteResponse.from(updatedNote);
    }

    /**
     * Archive (soft delete) a note
     */
    public void archiveNote(Long userId, Long noteId) {
        logger.info("Archiving note {} for user: {}", noteId, userId);

        Note note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + noteId));

        note.setIsArchived(true);
        noteRepository.save(note);

        logger.info("Note archived successfully: {}", noteId);
    }

    /**
     * Toggle pin status of a note
     */
    public NoteResponse togglePinNote(Long userId, Long noteId) {
        logger.info("Toggling pin status for note {} for user: {}", noteId, userId);

        Note note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + noteId));

        note.setIsPinned(!note.getIsPinned());
        Note updatedNote = noteRepository.save(note);

        logger.info("Note pin status toggled successfully: {} - isPinned: {}", noteId, updatedNote.getIsPinned());

        return NoteResponse.from(updatedNote);
    }

    /**
     * Search notes by title or content
     */
    public Page<NoteResponse> searchNotes(Long userId, String searchTerm, int page, int size) {
        logger.info("Searching notes for user: {}, term: '{}', page: {}, size: {}", userId, searchTerm, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Note> notes = noteRepository.searchNotesByUserId(userId, searchTerm, pageable);

        return notes.map(NoteResponse::from);
    }

    /**
     * Get notes by category
     */
    public Page<NoteResponse> getNotesByCategory(Long userId, String category, int page, int size) {
        logger.info("Fetching notes by category '{}' for user: {}, page: {}, size: {}", category, userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Note> notes = noteRepository.findNotesByUserIdAndCategory(userId, category, pageable);

        return notes.map(NoteResponse::from);
    }

    /**
     * Create enhanced note with checkbox support
     */
    public EnhancedNoteResponse createEnhancedNote(Long userId, EnhancedCreateNoteRequest request) {
        logger.info("Creating enhanced note for user: {}", userId);

        Note note = new Note(
                userId,
                request.getTitle() != null ? request.getTitle().trim() : null,
                request.getContent().trim(),
                request.getCategory() != null ? request.getCategory().trim() : null
        );

        // Set note type
        if (request.getNoteType() != null) {
            note.setNoteType(Note.NoteType.valueOf(request.getNoteType()));
        }

        // Set formatted content
        if (request.getFormattedContent() != null && !request.getFormattedContent().trim().isEmpty()) {
            note.setFormattedContent(request.getFormattedContent());
        }

        // Handle checkbox data
        if (request.getChecklistItems() != null && !request.getChecklistItems().isEmpty()) {
            try {
                CheckboxDataDto checkboxData = new CheckboxDataDto();
                request.getChecklistItems().forEach(item -> {
                    checkboxData.addItem(new org.example.dto.CheckboxItemDto(
                        item.getItemText(), item.getIsChecked(), item.getItemOrder()
                    ));
                });
                note.setCheckboxData(objectMapper.writeValueAsString(checkboxData));
                note.setHasChecklist(true);
            } catch (Exception e) {
                logger.error("Failed to serialize checkbox data", e);
                note.setCheckboxData(null);
                note.setHasChecklist(false);
            }
        } else {
            note.setCheckboxData(null);
            note.setHasChecklist(false);
        }

        Note savedNote = noteRepository.save(note);
        logger.info("Enhanced note created successfully with ID: {}", savedNote.getId());

        return new EnhancedNoteResponse(savedNote);
    }

    /**
     * Get enhanced note with checkbox data
     */
    public EnhancedNoteResponse getEnhancedNoteById(Long userId, Long noteId) {
        logger.info("Fetching enhanced note {} for user: {}", noteId, userId);

        Note note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + noteId));

        return new EnhancedNoteResponse(note);
    }

    /**
     * Update enhanced note without losing checkbox data
     */
    public EnhancedNoteResponse updateEnhancedNote(Long userId, Long noteId, EnhancedCreateNoteRequest request) {
        logger.info("Updating enhanced note {} for user: {}", noteId, userId);

        Note note = noteRepository.findByIdAndUserId(noteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + noteId));

        // Update basic fields
        note.setTitle(request.getTitle() != null ? request.getTitle().trim() : null);
        note.setContent(request.getContent().trim());
        note.setCategory(request.getCategory() != null ? request.getCategory().trim() : null);

        // Update checkbox data if provided
        if (request.getChecklistItems() != null) {
            if (request.getChecklistItems().isEmpty()) {
                note.setCheckboxData(null);
                note.setHasChecklist(false);
            } else {
                try {
                    CheckboxDataDto checkboxData = new CheckboxDataDto();
                    request.getChecklistItems().forEach(item -> {
                        checkboxData.addItem(new org.example.dto.CheckboxItemDto(
                            item.getItemText(), item.getIsChecked(), item.getItemOrder()
                        ));
                    });
                    note.setCheckboxData(objectMapper.writeValueAsString(checkboxData));
                    note.setHasChecklist(true);
                } catch (Exception e) {
                    logger.error("Failed to serialize checkbox data", e);
                }
            }
        }

        Note updatedNote = noteRepository.save(note);
        logger.info("Enhanced note updated successfully: {}", noteId);
        
        return new EnhancedNoteResponse(updatedNote);
    }

    /**
     * Get total count of active notes for a user
     */
    public Long getUserNotesCount(Long userId) {
        return noteRepository.countActiveNotesByUserId(userId);
    }

    /**
     * Save/Replace checklist items for a note
     */
    public List<ChecklistItemResponse> saveChecklistItems(Long userId, Long noteId, List<ChecklistItemRequest> items) {
        logger.info("Saving {} checklist items for note {} and user {}", items.size(), noteId, userId);

        // Verify note exists and belongs to user
        if (!noteRepository.existsByIdAndUserId(noteId, userId)) {
            throw new ResourceNotFoundException("Note not found with ID: " + noteId);
        }

        // Delete existing items for this note
        checklistItemRepository.deleteByNoteIdAndUserId(noteId, userId);

        // Map requests to entities
        List<ChecklistItem> checklistItems = new ArrayList<>();
        for (ChecklistItemRequest itemRequest : items) {
            ChecklistItem item = new ChecklistItem(
                noteId,
                userId,
                itemRequest.getItemText(),
                itemRequest.getIsChecked(),
                itemRequest.getItemOrder()
            );
            checklistItems.add(item);
        }

        // Save new items
        List<ChecklistItem> savedItems = checklistItemRepository.saveAll(checklistItems);
        
        // Also update the note's hasChecklist flag
        Note note = noteRepository.findById(noteId).orElse(null);
        if (note != null && !savedItems.isEmpty()) {
            note.setHasChecklist(true);
            noteRepository.save(note);
        }

        return savedItems.stream()
                .map(ChecklistItemResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Update a single checklist item
     */
    public ChecklistItemResponse updateChecklistItem(Long userId, Long itemId, ChecklistItemRequest request) {
        ChecklistItem item = checklistItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found with ID: " + itemId));

        item.setItemText(request.getItemText());
        item.setIsChecked(request.getIsChecked());
        if (request.getItemOrder() != null) {
            item.setItemOrder(request.getItemOrder());
        }

        ChecklistItem updatedItem = checklistItemRepository.save(item);
        return new ChecklistItemResponse(updatedItem);
    }

    /**
     * Delete a checklist item
     */
    public void deleteChecklistItem(Long userId, Long itemId) {
        ChecklistItem item = checklistItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found with ID: " + itemId));
        
        checklistItemRepository.delete(item);
    }
}