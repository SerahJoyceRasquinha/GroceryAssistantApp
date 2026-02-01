package com.grocery_assistant.your_grocery_assistant.controller;

import com.grocery_assistant.your_grocery_assistant.model.Category;
import com.grocery_assistant.your_grocery_assistant.repository.CategoryRepository;
import com.grocery_assistant.your_grocery_assistant.service.DatasetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final DatasetService datasetService;
    private final CategoryRepository categoryRepo;

    public RecommendationController(DatasetService datasetService, CategoryRepository categoryRepo) {
        this.datasetService = datasetService;
        this.categoryRepo = categoryRepo;
    }

    // REPLACE the existing categories method with this:
    @GetMapping("/categories")
    public List<String> categories(@RequestParam(required = false) String q) {
        List<String> allCategories = datasetService.getCategories();
        
        // If no query is provided (user just clicked), return all categories
        if (q == null || q.isBlank()) {
            return allCategories;
        }
        
        // Otherwise, filter as they type
        return allCategories.stream()
                .filter(c -> c.toLowerCase().contains(q.toLowerCase()))
                .limit(10)
                .toList();
    }

    @GetMapping("/items")
    public List<String> items(
            @RequestParam String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long categoryId
    ) {
        String targetCategory = category;

        // RESOLVE CATEGORY: If we have an ID, get the name (e.g., "Dairy") from the DB
        if (categoryId != null) {
            Optional<Category> dbCat = categoryRepo.findById(categoryId);
            if (dbCat.isPresent()) {
                targetCategory = dbCat.get().getName();
            }
        }

        List<String> source;

        // FILTER LOGIC: If the category name matches a column in grocery_list.csv, use ONLY those items.
        if (targetCategory != null && !targetCategory.isBlank() && datasetService.isKnownCategory(targetCategory)) {
            source = datasetService.getItemsByCategory(targetCategory);
        } else {
            // Otherwise, fallback to all items in the dataset
            source = datasetService.getAllItems();
        }

        return source.stream()
                .filter(i -> i.toLowerCase().contains(q.toLowerCase()))
                .distinct()
                .limit(10)
                .toList();
    }
}