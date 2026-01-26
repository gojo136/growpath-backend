package org.example.dto.response;

import org.example.entity.MediaAttachment;

import java.time.LocalDateTime;

public class MediaAttachmentResponse {

    private Long id;
    private Long noteId;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private Long fileSize;
    private String mimeType;
    private LocalDateTime createdAt;

    // Constructors
    public MediaAttachmentResponse() {
    }

    public MediaAttachmentResponse(MediaAttachment media) {
        this.id = media.getId();
        this.noteId = media.getNoteId();
        this.fileName = media.getFileName();
        this.fileType = media.getFileType();
        this.fileUrl = "/api/notes/media/" + media.getId(); // URL to download file
        this.fileSize = media.getFileSize();
        this.mimeType = media.getMimeType();
        this.createdAt = media.getCreatedAt();
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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
}
