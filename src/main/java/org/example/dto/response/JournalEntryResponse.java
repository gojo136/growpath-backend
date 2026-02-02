package org.example.dto.response;

import org.example.entity.JournalEntry;
import java.time.LocalDateTime;
import java.util.List;

public class JournalEntryResponse {
    private Long id;
    private String title;
    private String content;
    private String date;
    private JournalEntry.Mood mood;
    private String location;
    private List<String> photos;
    private String voiceNoteUrl;
    private LocalDateTime createdAt;

    public JournalEntryResponse(JournalEntry entry) {
        this.id = entry.getId();
        this.title = entry.getTitle();
        this.content = entry.getContent();
        this.date = entry.getDate();
        this.mood = entry.getMood();
        this.location = entry.getLocation();
        this.photos = entry.getPhotos();
        this.voiceNoteUrl = entry.getVoiceNoteUrl();
        this.createdAt = entry.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public JournalEntry.Mood getMood() { return mood; }
    public void setMood(JournalEntry.Mood mood) { this.mood = mood; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }

    public String getVoiceNoteUrl() { return voiceNoteUrl; }
    public void setVoiceNoteUrl(String voiceNoteUrl) { this.voiceNoteUrl = voiceNoteUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}