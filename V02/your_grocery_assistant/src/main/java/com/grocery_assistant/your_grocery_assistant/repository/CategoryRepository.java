package com.grocery_assistant.your_grocery_assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grocery_assistant.your_grocery_assistant.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
