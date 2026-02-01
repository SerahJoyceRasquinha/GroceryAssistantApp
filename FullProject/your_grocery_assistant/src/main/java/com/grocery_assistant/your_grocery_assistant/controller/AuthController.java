package com.grocery_assistant.your_grocery_assistant.controller;

import com.grocery_assistant.your_grocery_assistant.model.User;
import com.grocery_assistant.your_grocery_assistant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ================= LOGIN ================= */

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "registered", required = false) String registered,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        // 🔁 Convert ?error into flash + clean redirect
        if (error != null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Invalid username or password"
            );
            return "redirect:/login";
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }

        if (registered != null) {
            model.addAttribute("message", "Registration successful. Please login.");
        }

        return "login";
    }


    /* ================= REGISTER ================= */

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {

        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Account already exists. Please login.");
            model.addAttribute("username", username);
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
