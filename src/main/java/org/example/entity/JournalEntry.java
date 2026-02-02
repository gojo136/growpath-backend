package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String date; // YYYY-MM-DD

    @Enumerated(EnumType.STRING)
    private Mood mood;

    private String location;

    @ElementCollection
    @CollectionTable(name = "journal_photos", joinColumns = @JoinColumn(name = "journal_id"))
    @Column(name = "photo_url")
    private List<String> photos;

    private String voiceNoteUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Mood {
        HAPPY, SAD, PRODUCTIVE, RELAXED
    }

    // Constructors
    public JournalEntry() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Mood getMood() { return mood; }
    public void setMood(Mood mood) { this.mood = mood; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }

    public String getVoiceNoteUrl() { return voiceNoteUrl; }
    public void setVoiceNoteUrl(String voiceNoteUrl) { this.voiceNoteUrl = voiceNoteUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}