package com.grocery_assistant.your_grocery_assistant.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DatasetService {

    private List<String> categories;
    private Map<String, List<String>> categoryItems;
    private List<String> allItems;

    public DatasetService() {
        categories = new ArrayList<>();
        // Use CASE_INSENSITIVE_ORDER to handle "Dairy" vs "dairy" matches automatically
        categoryItems = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        allItems = new ArrayList<>();
    }

    @PostConstruct
    public void loadCsv() {
        try {
            // Using ClassPathResource is the standard way to read files from src/main/resources
            ClassPathResource resource = new ClassPathResource("dataset/grocery_list.csv");

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String headerLine = br.readLine();
                if (headerLine == null) return;

                String[] headers = headerLine.split(",");

                for (String h : headers) {
                    String key = h.trim();
                    if (!key.isEmpty()) {
                        categories.add(key);
                        categoryItems.put(key, new ArrayList<>());
                    }
                }

                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",", -1);

                    for (int i = 0; i < values.length && i < headers.length; i++) {
                        String value = values[i].trim();
                        if (!value.isEmpty()) {
                            String category = headers[i].trim();
                            categoryItems.get(category).add(value);
                            allItems.add(value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading dataset: " + e.getMessage());
        }
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getItemsByCategory(String category) {
        return categoryItems.getOrDefault(category, Collections.emptyList());
    }

    public boolean isKnownCategory(String category) {
        return category != null && categoryItems.containsKey(category.trim());
    }

    public List<String> getAllItems() {
        return allItems;
    }
}