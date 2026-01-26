package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "note_formatting",
        indexes = {
                @Index(name = "idx_formatting_note_id", columnList = "note_id"),
                @Index(name = "idx_formatting_user_id", columnList = "user_id")
        })
public class NoteFormatting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_position", nullable = false)
    @NotNull(message = "Start position is required")
    private Integer startPosition;

    @Column(name = "end_position", nullable = false)
    @NotNull(message = "End position is required")
    private Integer endPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "format_type", length = 20, nullable = false)
    private FormatType formatType;

    @Column(name = "format_value", length = 100)
    private String formatValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum FormatType {
        BOLD, ITALIC, UNDERLINE, HIGHLIGHT, COLOR, SIZE
    }

    // Constructors
    public NoteFormatting() {}

    public NoteFormatting(Long noteId, Long userId, Integer startPosition, Integer endPosition, 
                         FormatType formatType, String formatValue) {
        this.noteId = noteId;
        this.userId = userId;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.formatType = formatType;
        this.formatValue = formatValue;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }

    public FormatType getFormatType() {
        return formatType;
    }

    public void setFormatType(FormatType formatType) {
        this.formatType = formatType;
    }

    public String getFormatValue() {
        return formatValue;
    }

    public void setFormatValue(String formatValue) {
        this.formatValue = formatValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NoteFormatting{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", userId=" + userId +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", formatType=" + formatType +
                ", formatValue='" + formatValue + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}