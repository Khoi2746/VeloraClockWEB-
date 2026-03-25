package com.example.asm1.controller;

import com.example.asm1.Entity.RegisterForm;
import com.example.asm1.Entity.User; // <--- QUAN TRỌNG: Import Entity User
import com.example.asm1.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // 1. GET: Hiển thị trang đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpSession session) {
        String receivedEmail = (String) session.getAttribute("userEmail");

        // Chặn nếu người dùng chưa qua bước nhập email
        if (receivedEmail == null || receivedEmail.isEmpty()) {
            return "redirect:/checkMail";
        }

        RegisterForm form = new RegisterForm();
        form.setEmail(receivedEmail);
        model.addAttribute("registerForm", form);
        return "register";
    }

    // 2. POST: Xử lý đăng ký
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                                  BindingResult bindingResult,
                                  HttpSession session,
                                  Model model) {

        // --- BƯỚC 1: KIỂM TRA LỖI NHẬP LIỆU (VALIDATION) ---
        if (bindingResult.hasErrors()) {
            return "register"; // Trả về trang cũ để hiện lỗi đỏ
        }

        // --- BƯỚC 2: KIỂM TRA MÃ OTP ---
        String correctOtp = (String) session.getAttribute("otpCode");
        // Kiểm tra null để tránh lỗi nếu session hết hạn
        if (correctOtp == null || !correctOtp.equals(registerForm.getCode())) {
            bindingResult.rejectValue("code", "error.registerForm", "Mã xác nhận không đúng hoặc đã hết hạn!");
            return "register";
        }

        // --- BƯỚC 3: LƯU DATABASE ---
        try {
            User newUser = new User();
            
            // Map dữ liệu cơ bản
            newUser.setEmail(registerForm.getEmail());
            newUser.setPassword(registerForm.getPassword());
            newUser.setFirstName(registerForm.getFirstName());
            newUser.setSurname(registerForm.getSurname());
            newUser.setShoppingPreference(registerForm.getShoppingPreference());
            
            // Map ngày sinh (QUAN TRỌNG: code cũ của em thiếu đoạn này)
            newUser.setDobDay(registerForm.getDobDay());
            newUser.setDobMonth(registerForm.getDobMonth());
            newUser.setDobYear(registerForm.getDobYear());
            
            // Map checkbox
            newUser.setEmailSignup(registerForm.isEmailSignup());
            newUser.setAgreeTerms(registerForm.isAgreeTerms());

            // --- QUAN TRỌNG NHẤT: SET ROLE MẶC ĐỊNH ---
            // Nếu không có dòng này, SQL Server sẽ báo lỗi vì role_id không được null
            newUser.setRoleId(2); // Giả sử: 2 là User

            // Lưu vào SQL Server
            userRepository.save(newUser);

            // Dọn dẹp session
            session.removeAttribute("otpCode");
            session.removeAttribute("userEmail");

            System.out.println("LOG: Đăng ký thành công cho Member: " + newUser.getEmail());
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
            model.addAttribute("error", "Lỗi: Email này đã tồn tại hoặc lỗi hệ thống!");
            return "register";
        }
    }

    // ... Giữ nguyên các hàm Login, Logout, Change Password bên dưới ...
    // (Em copy lại các hàm bên dưới từ file cũ vào đây nhé)
    
    // 3. GET: Trang Login
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 4. POST: Xử lý Đăng nhập
    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        User userInDb = userRepository.findByEmail(email);
        if (userInDb != null && userInDb.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", userInDb);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Email hoặc mật khẩu không chính xác!");
            return "login";
        }
    }

    // 5. Đăng xuất
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    // --- ĐỔI MẬT KHẨU ---
    @GetMapping("/change-password")
    public String showChangePasswordForm(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam("currentPassword") String currentPassword,
                                        @RequestParam("newPassword") String newPassword,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        HttpSession session, Model model) {

        // Ép kiểu về User (Entity)
        User sessionUser = (User) session.getAttribute("loggedInUser");
        
        if (sessionUser == null) return "redirect:/login";

        if (!sessionUser.getPassword().equals(currentPassword)) {
            model.addAttribute("error", "Mật khẩu hiện tại không đúng!");
            return "change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Xác nhận mật khẩu mới không khớp!");
            return "change-password";
        }

        try {
            userRepository.updatePassword(sessionUser.getEmail(), newPassword);

            // Cập nhật lại session
            sessionUser.setPassword(newPassword);
            session.setAttribute("loggedInUser", sessionUser);

            model.addAttribute("success", "Đổi mật khẩu thành công");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi SQL: " + e.getMessage());
        }

        return "change-password";
    }

    // --- PROFILE ---
    @GetMapping("/profile")
    public String showProfile(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String processEditProfile(
            @RequestParam("firstName") String firstName,
            @RequestParam("surname") String surname,
            @RequestParam("shoppingPreference") String shoppingPreference,
            HttpSession session) {

        // Ép kiểu về User
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) return "redirect:/login";

        try {
            userRepository.updateProfile(firstName, surname, shoppingPreference, sessionUser.getEmail());

            // Tìm lại User mới nhất từ DB
            User updatedUser = userRepository.findByEmail(sessionUser.getEmail());

            if (updatedUser != null) {
                // Nếu User Entity có trường code (dù là @Transient), hãy set lại nếu cần giữ OTP cũ
                // updatedUser.setCode(sessionUser.getCode()); 
                session.setAttribute("loggedInUser", updatedUser);
            }

            System.out.println("Cập nhật thành công cho: " + firstName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/profile";
    }
}