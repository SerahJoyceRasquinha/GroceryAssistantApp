package com.grocery_assistant.your_grocery_assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // how often reminders should appear
    private int reminderIntervalDays;

    private LocalDateTime lastReminderTime;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    public Section() {}

    public Section(String name, int reminderIntervalDays) {
        this.name = name;
        this.reminderIntervalDays = reminderIntervalDays;
        this.lastReminderTime = LocalDateTime.now();
    }

    // GETTERS / SETTERS
    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getReminderIntervalDays() { return reminderIntervalDays; }

    public void setReminderIntervalDays(int reminderIntervalDays) {
        this.reminderIntervalDays = reminderIntervalDays;
    }

    public LocalDateTime getLastReminderTime() { return lastReminderTime; }

    public void setLastReminderTime(LocalDateTime lastReminderTime) {
        this.lastReminderTime = lastReminderTime;
    }

    public List<Category> getCategories() { return categories; }
}
