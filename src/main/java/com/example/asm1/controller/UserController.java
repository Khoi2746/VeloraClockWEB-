package com.example.asm1.controller;

import com.example.asm1.Entity.User;
import com.example.asm1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/management")
    public String management(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("user", new User());
        return "admin/User";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute User user) {

        // EDIT
        if (user.getId() != null) {
            User oldUser = userRepository.findById(user.getId())
                    .orElseThrow();

            // Không đổi password
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                user.setPassword(oldUser.getPassword());
            }
        }
        // CREATE
        else {
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                throw new RuntimeException("Password không được để trống");
            }
        }

        userRepository.save(user);
        return "redirect:/admin/user/management";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("user",
                userRepository.findById(id).orElseThrow());
        model.addAttribute("users", userRepository.findAll());
        return "admin/User";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/user/management";
    }
}