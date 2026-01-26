package org.example.dto.response;

import org.example.entity.NoteFormatting;

import java.time.LocalDateTime;

public class FormattingResponse {

    private Long id;
    private Long noteId;
    private Integer startPosition;
    private Integer endPosition;
    private String formatType;
    private String formatValue;
    private LocalDateTime createdAt;

    // Constructors
    public FormattingResponse() {}

    public FormattingResponse(NoteFormatting formatting) {
        this.id = formatting.getId();
        this.noteId = formatting.getNoteId();
        this.startPosition = formatting.getStartPosition();
        this.endPosition = formatting.getEndPosition();
        this.formatType = formatting.getFormatType() != null ? formatting.getFormatType().name() : null;
        this.formatValue = formatting.getFormatValue();
        this.createdAt = formatting.getCreatedAt();
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

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
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
}