package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChecklistItemRequest {

    @NotBlank(message = "Checklist item text is required")
    @Size(max = 1000, message = "Checklist item must not exceed 1000 characters")
    private String itemText;

    private Boolean isChecked = false;
    private Integer itemOrder = 0;

    // Constructors
    public ChecklistItemRequest() {}

    public ChecklistItemRequest(String itemText, Boolean isChecked, Integer itemOrder) {
        this.itemText = itemText;
        this.isChecked = isChecked != null ? isChecked : false;
        this.itemOrder = itemOrder != null ? itemOrder : 0;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "ChecklistItemRequest{" +
                "itemText='" + itemText + '\'' +
                ", isChecked=" + isChecked +
                ", itemOrder=" + itemOrder +
                '}';
    }
}