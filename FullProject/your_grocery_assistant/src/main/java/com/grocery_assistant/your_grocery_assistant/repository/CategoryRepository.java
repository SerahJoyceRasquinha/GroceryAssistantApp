package com.grocery_assistant.your_grocery_assistant.repository;

import com.grocery_assistant.your_grocery_assistant.model.Category;
import com.grocery_assistant.your_grocery_assistant.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndSection(Long id, Section section);
}
