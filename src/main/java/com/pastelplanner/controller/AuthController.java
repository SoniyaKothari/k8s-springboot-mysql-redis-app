package com.pastelplanner.controller;

import com.pastelplanner.model.User;
import com.pastelplanner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Handle login
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                // Save user in session
                session.setAttribute("user", user);
                return "redirect:/dashboard.html"; // static HTML redirect
            }
        }

        // Login failed
        model.addAttribute("message", "Invalid email or password");
        return "redirect:/index.html?error"; // optional: add query param for error
    }

    // Handle registration - FIXED: Added proper error handling and redirects
    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           HttpSession session,
                           Model model) {

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists");
            return "redirect:/index.html?error=email_exists";
        }

        try {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password);
            userRepository.save(newUser);

            // Save user in session
            session.setAttribute("user", newUser);

            return "redirect:/dashboard.html";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/index.html?error=registration_failed";
        }
    }

    // Add logout endpoint
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index.html";
    }
}