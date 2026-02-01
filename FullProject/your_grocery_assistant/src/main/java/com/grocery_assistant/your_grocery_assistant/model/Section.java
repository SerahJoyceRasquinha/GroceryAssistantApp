package com.grocery_assistant.your_grocery_assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Interval in days
    private int reminderIntervalDays;

    // Last reminder trigger time
    private LocalDateTime lastReminderTime;

    // ✅ NEW: User-selected reminder time (HH:mm)
    private LocalTime reminderTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    public Section() {}

    public Section(String name, int reminderIntervalDays, LocalTime reminderTime) {
        this.name = name;
        this.reminderIntervalDays = reminderIntervalDays;
        this.reminderTime = reminderTime;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayAtReminder = now.with(reminderTime).withSecond(0).withNano(0);

        if (todayAtReminder.isAfter(now)) {
            // If scheduled time is later today, set lastReminder back by the interval 
            // so it appears as "due" when that time hits today.
            this.lastReminderTime = todayAtReminder.minusDays(reminderIntervalDays);
        } else {
            // If it already passed today, set lastReminder to today so the 
            // interval starts counting from now.
            this.lastReminderTime = todayAtReminder;
        }
    }


    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getReminderIntervalDays() {
        return reminderIntervalDays;
    }

    public LocalDateTime getLastReminderTime() {
        return lastReminderTime;
    }

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReminderIntervalDays(int reminderIntervalDays) {
        this.reminderIntervalDays = reminderIntervalDays;
    }

    public void setLastReminderTime(LocalDateTime lastReminderTime) {
        this.lastReminderTime = lastReminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
