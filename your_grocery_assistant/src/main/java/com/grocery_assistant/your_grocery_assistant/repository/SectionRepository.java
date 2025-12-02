package com.grocery_assistant.your_grocery_assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grocery_assistant.your_grocery_assistant.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
}
