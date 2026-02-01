package com.grocery_assistant.your_grocery_assistant.controller;

import com.grocery_assistant.your_grocery_assistant.model.*;
import com.grocery_assistant.your_grocery_assistant.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class SectionController {

    private final SectionRepository sectionRepo;
    private final CategoryRepository categoryRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    public SectionController(SectionRepository sectionRepo,
                             CategoryRepository categoryRepo,
                             ItemRepository itemRepo,
                             UserRepository userRepo) {
        this.sectionRepo = sectionRepo;
        this.categoryRepo = categoryRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    // =========================
    // GROCERY SECTIONS PAGE
    // =========================
    @GetMapping("/grocery-sections")
    public String showSections(Model model, Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register"; // redirect to registration page if no user
        }
        User user = optionalUser.get();

        model.addAttribute("sections", sectionRepo.findByUser(user));
        return "grocery_sections";
    }

    // =========================
    // ADD SECTION
    // =========================
    @PostMapping("/add-section")
    public String addSection(@RequestParam String name,
                             @RequestParam int reminderIntervalDays,
                             @RequestParam int reminderHour,
                             @RequestParam int reminderMinute,
                             Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        LocalTime reminderTime = LocalTime.of(reminderHour, reminderMinute);
        Section section = new Section(name, reminderIntervalDays, reminderTime);
        section.setUser(user);

        sectionRepo.save(section);
        return "redirect:/grocery-sections";
    }

    // =========================
    // UPDATE REMINDER
    // =========================
    @PostMapping("/update-reminder")
    public String updateReminder(@RequestParam Long sectionId,
                                 @RequestParam int reminderIntervalDays,
                                 @RequestParam int reminderHour,
                                 @RequestParam int reminderMinute,
                                 Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        Section section = sectionRepo.findByIdAndUser(sectionId, user)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section"));

        LocalTime newTime = LocalTime.of(reminderHour, reminderMinute);
        section.setReminderIntervalDays(reminderIntervalDays);
        section.setReminderTime(newTime);

        // RESET LOGIC: Re-calculate lastReminderTime so changes take effect immediately
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayAtReminder = now.with(newTime).withSecond(0).withNano(0);

        if (todayAtReminder.isAfter(now)) {
            // If the new time is later today, make it eligible for today
            section.setLastReminderTime(todayAtReminder.minusDays(reminderIntervalDays));
        } else {
            // If the new time already passed today, the next one is in the future
            section.setLastReminderTime(todayAtReminder);
        }

        sectionRepo.save(section);
        return "redirect:/grocery-sections";
    }

    // =========================
    // HOME (REMINDERS)
    // =========================
    @GetMapping("/")
    public String home(Model model, Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        List<Section> sections = sectionRepo.findByUser(user);
        List<Section> dueSections = new ArrayList<>();
        //LocalDateTime now = LocalDateTime.now();

        // In SectionController.java -> home() method
        for (Section s : sections) {
            LocalDateTime now = LocalDateTime.now();
            LocalTime scheduledTime = s.getReminderTime();
            LocalDateTime last = s.getLastReminderTime();

            // 1. Calculate the next eligible reminder date based on the interval
            // (If interval is 7 days, it's due on or after last + 7 days)
            LocalDateTime nextEligibleDate = last.plusDays(s.getReminderIntervalDays());

            // 2. Check if we are at or past the date AND at or past the specific time of day
            if (!now.isBefore(nextEligibleDate)) {
                // If today is the day (or later), check if the current time has reached the scheduled time
                if (!now.toLocalTime().isBefore(scheduledTime)) {
                    dueSections.add(s);
                }
            }
        }

        model.addAttribute("reminders", dueSections);
        return "index";
    }

    // =========================
    // CLOSE REMINDER
    // =========================
    @PostMapping("/close-reminder")
    public String closeReminder(@RequestParam Long sectionId,
                                Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        Section section = sectionRepo.findByIdAndUser(sectionId, user)
                .orElseThrow();

        section.setLastReminderTime(LocalDateTime.now());
        sectionRepo.save(section);

        return "redirect:/";
    }

    // =========================
    // ADD CATEGORY
    // =========================
    @PostMapping("/add-category")
    public String addCategory(@RequestParam Long sectionId,
                              @RequestParam String name,
                              Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        Section section = sectionRepo.findByIdAndUser(sectionId, user)
                .orElseThrow();

        categoryRepo.save(new Category(name, section));
        return "redirect:/grocery-sections";
    }

    // =========================
    // ADD ITEM
    // =========================
    @PostMapping("/add-item")
    public String addItem(@RequestParam Long categoryId,
                          @RequestParam String name,
                          Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow();

        if (!category.getSection().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }

        itemRepo.save(new Item(name, category));
        return "redirect:/grocery-sections";
    }

    // =========================
    // DELETE SECTION
    // =========================
    @PostMapping("/delete-section")
    public String deleteSection(@RequestParam Long sectionId,
                                Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        Section section = sectionRepo.findByIdAndUser(sectionId, user)
                .orElseThrow();

        sectionRepo.delete(section);
        return "redirect:/grocery-sections";
    }

    // =========================
    // DELETE CATEGORY
    // =================
    @PostMapping("/delete-category")
    public String deleteCategory(@RequestParam Long categoryId,
                                 Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow();

        if (!category.getSection().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }

        categoryRepo.delete(category);
        return "redirect:/grocery-sections";
    }

    // =========================
    // DELETE ITEM
    // =================
    @PostMapping("/delete-item")
    public String deleteItem(@RequestParam Long itemId,
                             Authentication auth) {

        Optional<User> optionalUser = userRepo.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/register";
        }
        User user = optionalUser.get();

        Item item = itemRepo.findById(itemId)
                .orElseThrow();

        if (!item.getCategory().getSection().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }

        itemRepo.delete(item);
        return "redirect:/grocery-sections";
    }
}
