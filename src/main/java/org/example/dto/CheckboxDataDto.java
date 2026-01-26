package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class CheckboxDataDto {
    
    @JsonProperty("items")
    private List<CheckboxItemDto> items;

    public CheckboxDataDto() {
        this.items = new ArrayList<>();
    }

    public CheckboxDataDto(List<CheckboxItemDto> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public List<CheckboxItemDto> getItems() {
        return items;
    }

    public void setItems(List<CheckboxItemDto> items) {
        this.items = items;
    }

    public void addItem(CheckboxItemDto item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    public int getTotalItems() {
        return items != null ? items.size() : 0;
    }

    public int getCompletedItems() {
        if (items == null) return 0;
        return (int) items.stream().filter(item -> Boolean.TRUE.equals(item.getChecked())).count();
    }
}