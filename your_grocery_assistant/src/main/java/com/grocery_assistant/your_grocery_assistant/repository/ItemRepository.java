package com.grocery_assistant.your_grocery_assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grocery_assistant.your_grocery_assistant.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {}
