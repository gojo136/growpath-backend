package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_attachments", indexes = {
        @Index(name = "idx_media_note_id", columnList = "note_id"),
        @Index(name = "idx_media_user_id", columnList = "user_id"),
        @Index(name = "idx_media_type", columnList = "file_type")
})
public class MediaAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note_id", nullable = false)
    @NotNull(message = "Note ID is required")
    private Long noteId;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private Long userId;

    @Column(name = "file_name", nullable = false, length = 255)
    @NotBlank(message = "File name is required")
    private String fileName;

    @Column(name = "file_type", nullable = false, length = 20)
    @NotBlank(message = "File type is required")
    private String fileType; // IMAGE, VOICE

    @Column(name = "file_path", nullable = false, length = 500)
    @NotBlank(message = "File path is required")
    private String filePath; // Supabase public URL

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public MediaAttachment() {
    }

    public MediaAttachment(Long noteId, Long userId, String fileName, String fileType,
            String filePath, Long fileSize, String mimeType) {
        this.noteId = noteId;
        this.userId = userId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    @Override
    public String toString() {
        return "MediaAttachment{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", userId=" + userId +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", createdAt=" + createdAt +
                '}';
    }
}
