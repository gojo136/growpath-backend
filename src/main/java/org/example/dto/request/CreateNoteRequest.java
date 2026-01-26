package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateNoteRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content must not exceed 10000 characters")
    private String content;

    @Size(max = 500, message = "Category must not exceed 500 characters")
    private String category;

    // Constructors
    public CreateNoteRequest() {}

    public CreateNoteRequest(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "CreateNoteRequest{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}