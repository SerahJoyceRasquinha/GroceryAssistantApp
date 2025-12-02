package com.grocery_assistant.your_grocery_assistant.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    public Category() {}

    public Category(String name, Section section) {
        this.name = name;
        this.section = section;
    }

    // getters & setters
    public Long getId() { return id; }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public List<Item> getItems() { return items; }
}
