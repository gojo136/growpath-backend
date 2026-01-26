package org.example.dto.response;

import org.example.entity.Note;

import java.time.LocalDateTime;

public class NoteResponse {

    private Long id;
    private String title;
    private String content;
    private String category;
    private Boolean isPinned;
    private Boolean isArchived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enhanced fields
    private String formattedContent;
    private String noteType; // TEXT, CHECKLIST, MIXED
    private Boolean hasImages;
    private Boolean hasVoiceNotes;
    private Boolean hasChecklist;
    private Boolean hasFormatting;

    // Constructors
    public NoteResponse() {
    }

    public NoteResponse(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.category = note.getCategory();
        this.isPinned = note.getIsPinned();
        this.isArchived = note.getIsArchived();
        this.createdAt = note.getCreatedAt();
        this.updatedAt = note.getUpdatedAt();

        // Map enhanced fields
        this.formattedContent = note.getFormattedContent();
        this.noteType = note.getNoteType() != null ? note.getNoteType().name() : "TEXT";
        this.hasImages = note.getHasImages();
        this.hasVoiceNotes = note.getHasVoiceNotes();
        this.hasChecklist = note.getHasChecklist();
        this.hasFormatting = note.getHasFormatting();
    }

    public NoteResponse(Long id, String title, String content, String category,
            Boolean isPinned, Boolean isArchived,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.isPinned = isPinned;
        this.isArchived = isArchived;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Static factory method
    public static NoteResponse from(Note note) {
        return new NoteResponse(note);
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getFormattedContent() {
        return formattedContent;
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
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

    @Override
    public String toString() {
        return "NoteResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", isPinned=" + isPinned +
                ", isArchived=" + isArchived +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", hasImages=" + hasImages +
                ", hasVoiceNotes=" + hasVoiceNotes +
                ", hasChecklist=" + hasChecklist +
                '}';
    }
}