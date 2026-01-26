package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notes",
        indexes = {
                @Index(name = "idx_notes_user_id", columnList = "user_id"),
                @Index(name = "idx_notes_archived", columnList = "is_archived"),
                @Index(name = "idx_notes_pinned", columnList = "is_pinned"),
                @Index(name = "idx_notes_type", columnList = "note_type"),
                @Index(name = "idx_notes_features", columnList = "has_images, has_voice_notes, has_checklist")
        })
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 200)
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String formattedContent;

    @Column(length = 50)
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "note_type", length = 20)
    private NoteType noteType = NoteType.TEXT;

    @Column(name = "has_images")
    private Boolean hasImages = false;

    @Column(name = "has_voice_notes")
    private Boolean hasVoiceNotes = false;

    @Column(name = "has_checklist")
    private Boolean hasChecklist = false;

    @Column(name = "has_formatting")
    private Boolean hasFormatting = false;

    @Column(columnDefinition = "JSONB")
    private String checkboxData; // JSON string for checkbox items

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "noteId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MediaAttachment> mediaAttachments;

    @OneToMany(mappedBy = "noteId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChecklistItem> checklistItems;

    @OneToMany(mappedBy = "noteId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NoteFormatting> formattingList;

    public enum NoteType {
        TEXT, CHECKLIST, MIXED
    }

    // Constructors
    public Note() {}

    public Note(Long userId, String title, String content, String category) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.noteType = NoteType.TEXT;
        this.isPinned = false;
        this.isArchived = false;
        this.hasImages = false;
        this.hasVoiceNotes = false;
        this.hasChecklist = false;
        this.hasFormatting = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        this.hasFormatting = (formattedContent != null && !formattedContent.trim().isEmpty());
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public void setNoteType(NoteType noteType) {
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

    public List<MediaAttachment> getMediaAttachments() {
        return mediaAttachments;
    }

    public void setMediaAttachments(List<MediaAttachment> mediaAttachments) {
        this.mediaAttachments = mediaAttachments;
    }

    public List<ChecklistItem> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(List<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

    public List<NoteFormatting> getFormattingList() {
        return formattingList;
    }

    public void setFormattingList(List<NoteFormatting> formattingList) {
        this.formattingList = formattingList;
    }

    public String getCheckboxData() {
        return checkboxData;
    }

    public void setCheckboxData(String checkboxData) {
        this.checkboxData = checkboxData;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", noteType=" + noteType +
                ", hasImages=" + hasImages +
                ", hasVoiceNotes=" + hasVoiceNotes +
                ", hasChecklist=" + hasChecklist +
                ", hasFormatting=" + hasFormatting +
                ", isPinned=" + isPinned +
                ", isArchived=" + isArchived +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}