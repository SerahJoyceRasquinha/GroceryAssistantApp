package com.grocery_assistant.your_grocery_assistant.model;

import jakarta.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // item availability when user checks reminder
    private boolean available = true;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Item() {}

    public Item(String name, Category category) {
        this.name = name;
        this.category = category;
        this.available = true;
    }

    // getters & setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public boolean isAvailable() { return available; }

    public void setName(String name) { this.name = name; }
    public void setAvailable(boolean available) { this.available = available; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
