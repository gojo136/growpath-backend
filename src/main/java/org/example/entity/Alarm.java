package org.example.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "alarms")
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String time;

    @Column(name = "am_pm", nullable = false)
    private String amPm;

    @Column(name = "repeat_days")
    private String repeatDays;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "alarm_sound")
    private Boolean alarmSound;

    @Column(name = "vibration_pattern")
    private Boolean vibrationPattern;

    @Column(name = "math_mission")
    private Boolean mathMission;

    @Column(name = "word_complete")
    private Boolean wordComplete;

    @Column(name = "alarm_note")
    private String alarmNote;

    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Alarm() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmPm() {
        return amPm;
    }

    public void setAmPm(String amPm) {
        this.amPm = amPm;
    }

    public String getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Boolean getAlarmSound() {
        return alarmSound;
    }

    public void setAlarmSound(Boolean alarmSound) {
        this.alarmSound = alarmSound;
    }

    public Boolean getVibrationPattern() {
        return vibrationPattern;
    }

    public void setVibrationPattern(Boolean vibrationPattern) {
        this.vibrationPattern = vibrationPattern;
    }

    public Boolean getMathMission() {
        return mathMission;
    }

    public void setMathMission(Boolean mathMission) {
        this.mathMission = mathMission;
    }

    public Boolean getWordComplete() {
        return wordComplete;
    }

    public void setWordComplete(Boolean wordComplete) {
        this.wordComplete = wordComplete;
    }

    public String getAlarmNote() {
        return alarmNote;
    }

    public void setAlarmNote(String alarmNote) {
        this.alarmNote = alarmNote;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
