package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "checklist_items",
        indexes = {
                @Index(name = "idx_checklist_note_id", columnList = "note_id"),
                @Index(name = "idx_checklist_user_id", columnList = "user_id"),
                @Index(name = "idx_checklist_order", columnList = "note_id, item_order")
        })
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "item_text", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Checklist item text is required")
    @Size(max = 1000, message = "Checklist item must not exceed 1000 characters")
    private String itemText;

    @Column(name = "is_checked", nullable = false)
    private Boolean isChecked = false;

    @Column(name = "item_order")
    private Integer itemOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors


    public ChecklistItem(Long noteId, Long userId, String itemText, Boolean isChecked, Integer itemOrder) {
        this.noteId = noteId;
        this.userId = userId;
        this.itemText = itemText;
        this.isChecked = isChecked != null ? isChecked : false;
        this.itemOrder = itemOrder != null ? itemOrder : 0;
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

    @Override
    public String toString() {
        return "ChecklistItem{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", userId=" + userId +
                ", itemText='" + itemText + '\'' +
                ", isChecked=" + isChecked +
                ", itemOrder=" + itemOrder +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}