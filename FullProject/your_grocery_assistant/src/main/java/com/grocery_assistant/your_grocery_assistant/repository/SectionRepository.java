package com.grocery_assistant.your_grocery_assistant.repository;

import com.grocery_assistant.your_grocery_assistant.model.Section;
import com.grocery_assistant.your_grocery_assistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByUser(User user);

    Optional<Section> findByIdAndUser(Long id, User user);
}
