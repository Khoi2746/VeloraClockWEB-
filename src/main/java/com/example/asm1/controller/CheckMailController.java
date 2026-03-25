package com.example.asm1.controller;

import com.example.asm1.Entity.MailForm;
import com.example.asm1.service.MailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckMailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/checkMail")
    public String showCheckMail(Model model, HttpSession session) {

        model.addAttribute("ChangeMail", new MailForm());

        String error = (String) session.getAttribute("sessionError");
        if (error != null) {
            model.addAttribute("errorMessage", error);
            session.removeAttribute("sessionError");
        }

        return "ChangeMail";
    }

    @PostMapping("/login/check-email")
    public String checkEmail(
            @Valid @ModelAttribute("ChangeMail") MailForm mailForm,
            BindingResult bindingResult,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "ChangeMail";
        }

        String email = mailForm.getEmail().trim();

        try {
            String otpCode = mailService.sendOtp(email);

            // ✅ LƯU SESSION
            session.setAttribute("userEmail", email);
            session.setAttribute("otpCode", otpCode);

            System.out.println("SESSION OTP = " + otpCode);

            return "redirect:/register";

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("sessionError", "Không gửi được email!");
            return "redirect:/checkMail";
        }
    }
}
