package com.grocery_assistant.your_grocery_assistant.controller;

import com.grocery_assistant.your_grocery_assistant.model.Category;
import com.grocery_assistant.your_grocery_assistant.model.Section;
import com.grocery_assistant.your_grocery_assistant.model.Item;

import com.grocery_assistant.your_grocery_assistant.repository.CategoryRepository;
import com.grocery_assistant.your_grocery_assistant.repository.SectionRepository;
import com.grocery_assistant.your_grocery_assistant.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SectionController {

    private final SectionRepository sectionRepo;
    private final CategoryRepository categoryRepo;
    private final ItemRepository itemRepo;   // ✅ NEW

    // Updated constructor to include itemRepo
    public SectionController(SectionRepository sectionRepo,
                             CategoryRepository categoryRepo,
                             ItemRepository itemRepo) {
        this.sectionRepo = sectionRepo;
        this.categoryRepo = categoryRepo;
        this.itemRepo = itemRepo;
    }

    // Show grocery sections page
    @GetMapping("/grocery-sections")
    public String showSections(Model model) {
        model.addAttribute("sections", sectionRepo.findAll());
        return "grocery_sections"; // thymeleaf template name
    }

    // Add new section
    @PostMapping("/add-section")
    public String addSection(@RequestParam String name,
                            @RequestParam int reminderIntervalDays) {

        Section section = new Section(name, reminderIntervalDays);
        sectionRepo.save(section);

        return "redirect:/grocery-sections";
    }

    @PostMapping("/update-reminder")
    public String updateReminder(@RequestParam Long sectionId,
                                @RequestParam int reminderIntervalDays) {

        Section s = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));

        s.setReminderIntervalDays(reminderIntervalDays);
        sectionRepo.save(s);

        return "redirect:/grocery-sections";
    }

    @GetMapping("/")
    public String home(Model model) {

        List<Section> all = sectionRepo.findAll();
        List<Section> dueSections = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Section s : all) {
            if (s.getLastReminderTime() == null) {
                s.setLastReminderTime(now);
                sectionRepo.save(s);
                continue;
            }

            if (s.getLastReminderTime().plusDays(s.getReminderIntervalDays()).isBefore(now)) {
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




    // Add category to a section
    @PostMapping("/add-category")
    public String addCategory(@RequestParam Long sectionId, @RequestParam String name) {

        Section section = sectionRepo.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid section ID"));

        Category category = new Category(name, section);
        categoryRepo.save(category);

        return "redirect:/grocery-sections";
    }

    // ✅ NEW: Add item to a category
    @PostMapping("/add-item")
    public String addItem(@RequestParam Long categoryId, @RequestParam String name) {

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

        itemRepo.save(new Item(name, category));

        return "redirect:/grocery-sections";
    }

        // DELETE SECTION
    @PostMapping("/delete-section")
    public String deleteSection(@RequestParam Long sectionId) {
        sectionRepo.deleteById(sectionId);
        return "redirect:/grocery-sections";
    }

    // DELETE CATEGORY
    @PostMapping("/delete-category")
    public String deleteCategory(@RequestParam Long categoryId) {
        categoryRepo.deleteById(categoryId);
        return "redirect:/grocery-sections";
    }

    // DELETE ITEM
    @PostMapping("/delete-item")
    public String deleteItem(@RequestParam Long itemId) {
        itemRepo.deleteById(itemId);
        return "redirect:/grocery-sections";
    }

}
