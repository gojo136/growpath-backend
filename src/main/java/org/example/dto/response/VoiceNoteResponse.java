package org.example.dto.response;

import java.time.LocalDateTime;

public class VoiceNoteResponse {
    private Long id;
    private String title;
    private String filePath;
    private Long durationMs;
    private String category;
    private LocalDateTime createdAt;

    public VoiceNoteResponse(Long id, String title, String filePath, Long durationMs, String category, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
        this.durationMs = durationMs;
        this.category = category;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getFilePath() { return filePath; }
    public Long getDurationMs() { return durationMs; }
    public String getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
