package org.example.dto.response;

import org.example.entity.Note;
import org.example.entity.MediaAttachment;
import org.example.entity.ChecklistItem;
import org.example.entity.NoteFormatting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EnhancedNoteResponse {

    private Long id;
    private String title;
    private String content;
    private String formattedContent;
    private String category;
    private String noteType;
    private Boolean isPinned;
    private Boolean isArchived;
    private Boolean hasImages;
    private Boolean hasVoiceNotes;
    private Boolean hasChecklist;
    private Boolean hasFormatting;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Related data
    private List<MediaAttachmentResponse> mediaAttachments;
    private List<ChecklistItemResponse> checklistItems;
    private List<FormattingResponse> formatting;

    // Constructors
    public EnhancedNoteResponse() {}

    public EnhancedNoteResponse(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.formattedContent = note.getFormattedContent();
        this.category = note.getCategory();
        this.noteType = note.getNoteType() != null ? note.getNoteType().name() : "TEXT";
        this.isPinned = note.getIsPinned();
        this.isArchived = note.getIsArchived();
        this.hasImages = note.getHasImages();
        this.hasVoiceNotes = note.getHasVoiceNotes();
        this.hasChecklist = note.getHasChecklist();
        this.hasFormatting = note.getHasFormatting();
        this.createdAt = note.getCreatedAt();
        this.updatedAt = note.getUpdatedAt();

        // Convert related entities
        if (note.getMediaAttachments() != null) {
            this.mediaAttachments = note.getMediaAttachments().stream()
                    .map(MediaAttachmentResponse::new)
                    .collect(Collectors.toList());
        }

        if (note.getChecklistItems() != null) {
            this.checklistItems = note.getChecklistItems().stream()
                    .map(ChecklistItemResponse::new)
                    .collect(Collectors.toList());
        }

        if (note.getFormattingList() != null) {
            this.formatting = note.getFormattingList().stream()
                    .map(FormattingResponse::new)
                    .collect(Collectors.toList());
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormattedContent() {
        return formattedContent;
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Boolean getHasImages() {
        return hasImages;
    }

    public void setHasImages(Boolean hasImages) {
        this.hasImages = hasImages;
    }

    public Boolean getHasVoiceNotes() {
        return hasVoiceNotes;
    }

    public void setHasVoiceNotes(Boolean hasVoiceNotes) {
        this.hasVoiceNotes = hasVoiceNotes;
    }

    public Boolean getHasChecklist() {
        return hasChecklist;
    }

    public void setHasChecklist(Boolean hasChecklist) {
        this.hasChecklist = hasChecklist;
    }

    public Boolean getHasFormatting() {
        return hasFormatting;
    }

    public void setHasFormatting(Boolean hasFormatting) {
        this.hasFormatting = hasFormatting;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<MediaAttachmentResponse> getMediaAttachments() {
        return mediaAttachments;
    }

    public void setMediaAttachments(List<MediaAttachmentResponse> mediaAttachments) {
        this.mediaAttachments = mediaAttachments;
    }

    public List<ChecklistItemResponse> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(List<ChecklistItemResponse> checklistItems) {
        this.checklistItems = checklistItems;
    }

    public List<FormattingResponse> getFormatting() {
        return formatting;
    }

    public void setFormatting(List<FormattingResponse> formatting) {
        this.formatting = formatting;
    }
}