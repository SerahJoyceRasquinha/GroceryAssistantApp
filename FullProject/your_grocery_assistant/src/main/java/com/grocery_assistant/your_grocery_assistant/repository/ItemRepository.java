package com.grocery_assistant.your_grocery_assistant.repository;

import com.grocery_assistant.your_grocery_assistant.model.Item;
import com.grocery_assistant.your_grocery_assistant.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndCategory(Long id, Category category);
}
