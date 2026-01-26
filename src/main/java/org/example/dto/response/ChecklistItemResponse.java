package org.example.dto.response;

import org.example.entity.ChecklistItem;

import java.time.LocalDateTime;

public class ChecklistItemResponse {

    private Long id;
    private Long noteId;
    private String itemText;
    private Boolean isChecked;
    private Integer itemOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ChecklistItemResponse() {}

    public ChecklistItemResponse(ChecklistItem item) {
        this.id = item.getId();
        this.noteId = item.getNoteId();
        this.itemText = item.getItemText();
        this.isChecked = item.getIsChecked();
        this.itemOrder = item.getItemOrder();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
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

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
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
}