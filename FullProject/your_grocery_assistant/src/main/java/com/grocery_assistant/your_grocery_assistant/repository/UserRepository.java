package com.grocery_assistant.your_grocery_assistant.repository;

import com.grocery_assistant.your_grocery_assistant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
