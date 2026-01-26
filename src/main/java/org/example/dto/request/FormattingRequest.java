package org.example.dto.request;

import jakarta.validation.constraints.NotNull;

public class FormattingRequest {

    @NotNull(message = "Start position is required")
    private Integer startPosition;

    @NotNull(message = "End position is required")
    private Integer endPosition;

    @NotNull(message = "Format type is required")
    private String formatType; // BOLD, ITALIC, UNDERLINE, HIGHLIGHT, COLOR, SIZE

    private String formatValue; // For colors, sizes, etc.

    // Constructors
    public FormattingRequest() {}

    public FormattingRequest(Integer startPosition, Integer endPosition, String formatType, String formatValue) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.formatType = formatType;
        this.formatValue = formatValue;
    }

    // Getters and Setters
    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public String getFormatValue() {
        return formatValue;
    }

    public void setFormatValue(String formatValue) {
        this.formatValue = formatValue;
    }

    @Override
    public String toString() {
        return "FormattingRequest{" +
                "startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", formatType='" + formatType + '\'' +
                ", formatValue='" + formatValue + '\'' +
                '}';
    }
}