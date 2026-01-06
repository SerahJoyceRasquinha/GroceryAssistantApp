package com.grocery_assistant.your_grocery_assistant.controller;

import com.grocery_assistant.your_grocery_assistant.model.Category;
import com.grocery_assistant.your_grocery_assistant.model.Section;
import com.grocery_assistant.your_grocery_assistant.model.Item;
import com.grocery_assistant.your_grocery_assistant.repository.CategoryRepository;
import com.grocery_assistant.your_grocery_assistant.repository.SectionRepository;
import com.grocery_assistant.your_grocery_assistant.repository.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SectionController {

    private final SectionRepository sectionRepo;
    private final CategoryRepository categoryRepo;
    private final ItemRepository itemRepo;

    public SectionController(SectionRepository sectionRepo,
                             CategoryRepository categoryRepo,
                             ItemRepository itemRepo) {
        this.sectionRepo = sectionRepo;
        this.categoryRepo = categoryRepo;
        this.itemRepo = itemRepo;
    }

    @GetMapping("/grocery-sections")
    public String showSections(Model model) {
        model.addAttribute("sections", sectionRepo.findAll());
        return "grocery_sections";
    }

    @PostMapping("/add-section")
    public String addSection(@RequestParam String name,
                             @RequestParam int reminderIntervalDays,
                             @RequestParam int reminderHour,
                             @RequestParam int reminderMinute) {

        LocalTime reminderTime = LocalTime.of(reminderHour, reminderMinute);
        Section section = new Section(name, reminderIntervalDays, reminderTime);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayAtReminder = now.withHour(reminderTime.getHour())
                                          .withMinute(reminderTime.getMinute())
                                          .withSecond(0)
                                          .withNano(0);

        if (todayAtReminder.isAfter(now)) {
            section.setLastReminderTime(todayAtReminder.minusDays(reminderIntervalDays));
        } else {
            section.setLastReminderTime(todayAtReminder);
        }

        sectionRepo.save(section);
        return "redirect:/grocery-sections";
    }

    @PostMapping("/update-reminder")
    public String updateReminder(@RequestParam Long sectionId,
                                 @RequestParam int reminderIntervalDays,
                                 @RequestParam int reminderHour,
                                 @RequestParam int reminderMinute) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));

        section.setReminderIntervalDays(reminderIntervalDays);

        LocalTime newTime = LocalTime.of(reminderHour, reminderMinute);
        section.setReminderTime(newTime);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayAtReminder = now.withHour(newTime.getHour())
                                          .withMinute(newTime.getMinute())
                                          .withSecond(0)
                                          .withNano(0);

        if (todayAtReminder.isAfter(now)) {
            section.setLastReminderTime(todayAtReminder.minusDays(reminderIntervalDays));
        } else {
            section.setLastReminderTime(todayAtReminder.minusDays(reminderIntervalDays - 1));
        }

        sectionRepo.save(section);
        return "redirect:/grocery-sections";
    }


    @GetMapping("/")
    public String home(Model model) {

        List<Section> all = sectionRepo.findAll();
        List<Section> dueSections = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Section s : all) {

            LocalTime reminderTime = s.getReminderTime();
            if (reminderTime == null) {
                reminderTime = LocalTime.of(8, 0);
                s.setReminderTime(reminderTime);
                s.setLastReminderTime(now.withHour(8).withMinute(0).withSecond(0).withNano(0));
                sectionRepo.save(s);
            }

            if (s.getLastReminderTime() == null) {
                s.setLastReminderTime(
                        now.withHour(reminderTime.getHour())
                           .withMinute(reminderTime.getMinute())
                           .withSecond(0)
                           .withNano(0)
                );
                sectionRepo.save(s);
                continue;
            }

            
            LocalDateTime nextReminder = s.getLastReminderTime()
                    .plusDays(s.getReminderIntervalDays())
                    .withHour(reminderTime.getHour())
                    .withMinute(reminderTime.getMinute())
                    .withSecond(0)
                    .withNano(0);

            
            LocalDateTime todayReminder = now.withHour(reminderTime.getHour())
                                             .withMinute(reminderTime.getMinute())
                                             .withSecond(0)
                                             .withNano(0);

            // Add to due sections if:
            // 1. Next reminder (interval) has passed
            // OR
            // 2. Today’s reminder time passed, and lastReminderTime < todayReminder
            if (nextReminder.isBefore(now) || (todayReminder.isBefore(now) && s.getLastReminderTime().isBefore(todayReminder))) {
                dueSections.add(s);
            }
        }

        model.addAttribute("reminders", dueSections);
        return "index";
    }

    @PostMapping("/close-reminder")
    public String closeReminder(@RequestParam Long sectionId) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));

        section.setLastReminderTime(LocalDateTime.now());
        sectionRepo.save(section);

        return "redirect:/";
    }

    @PostMapping("/add-category")
    public String addCategory(@RequestParam Long sectionId,
                              @RequestParam String name) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));

        categoryRepo.save(new Category(name, section));
        return "redirect:/grocery-sections";
    }

   
    @PostMapping("/add-item")
    public String addItem(@RequestParam Long categoryId,
                          @RequestParam String name) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        itemRepo.save(new Item(name, category));
        return "redirect:/grocery-sections";
    }

    
    @PostMapping("/delete-section")
    public String deleteSection(@RequestParam Long sectionId) {
        sectionRepo.deleteById(sectionId);
        return "redirect:/grocery-sections";
    }

    @PostMapping("/delete-category")
    public String deleteCategory(@RequestParam Long categoryId) {
        categoryRepo.deleteById(categoryId);
        return "redirect:/grocery-sections";
    }

    @PostMapping("/delete-item")
    public String deleteItem(@RequestParam Long itemId) {
        itemRepo.deleteById(itemId);
        return "redirect:/grocery-sections";
    }
}
