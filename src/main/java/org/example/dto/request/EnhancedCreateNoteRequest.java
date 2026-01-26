package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class EnhancedCreateNoteRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    private String formattedContent;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    private String noteType; // TEXT, CHECKLIST, MIXED

    private List<ChecklistItemRequest> checklistItems;
    private List<FormattingRequest> formatting;

    // Constructors
    public EnhancedCreateNoteRequest() {}

    public EnhancedCreateNoteRequest(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.noteType = "TEXT";
    }

    // Getters and Setters
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

    public String getFormattedContent() {
        return formattedContent;
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public List<ChecklistItemRequest> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(List<ChecklistItemRequest> checklistItems) {
        this.checklistItems = checklistItems;
    }

    public List<FormattingRequest> getFormatting() {
        return formatting;
    }

    public void setFormatting(List<FormattingRequest> formatting) {
        this.formatting = formatting;
    }

    @Override
    public String toString() {
        return "EnhancedCreateNoteRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", noteType='" + noteType + '\'' +
                ", checklistItems=" + (checklistItems != null ? checklistItems.size() : 0) +
                ", formatting=" + (formatting != null ? formatting.size() : 0) +
                '}';
    }
}