package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class CheckboxItemDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("checked")
    private Boolean checked;
    
    @JsonProperty("position")
    private Integer position;

    public CheckboxItemDto() {
        this.id = UUID.randomUUID().toString();
        this.checked = false;
        this.position = 0;
    }

    public CheckboxItemDto(String text, Boolean checked, Integer position) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.checked = checked != null ? checked : false;
        this.position = position != null ? position : 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Boolean getChecked() { return checked; }
    public void setChecked(Boolean checked) { this.checked = checked; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
}