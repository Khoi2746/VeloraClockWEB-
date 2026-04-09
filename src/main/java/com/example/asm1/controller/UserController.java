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




// package com.example.asm1.controller;

// import com.example.asm1.Entity.User;
// import com.example.asm1.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/admin/users")
// public class UserRestController {

//     @Autowired
//     private UserRepository userRepository;

//     // 1. Lấy danh sách tất cả người dùng
//     @GetMapping
//     public List<User> getAllUsers() {
//         return userRepository.findAll();
//     }

//     // 2. Lấy chi tiết một người dùng theo ID
//     @GetMapping("/{id}")
//     public ResponseEntity<User> getUserById(@PathVariable Long id) {
//         return userRepository.findById(id)
//                 .map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }

//     // 3. Tạo mới người dùng (POST)
//     @PostMapping
//     public ResponseEntity<?> createUser(@RequestBody User user) {
//         if (user.getPassword() == null || user.getPassword().isBlank()) {
//             return ResponseEntity.badRequest().body("Mật khẩu không được để trống khi tạo mới!");
//         }
//         // Mặc định roleId nếu không truyền (ví dụ 2 là khách hàng)
//         if (user.getRoleId() == null) {
//             user.setRoleId(2);
//         }
//         return ResponseEntity.ok(userRepository.save(user));
//     }

//     // 4. Cập nhật người dùng (PUT)
//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
//         return userRepository.findById(id).map(existingUser -> {
//             // Cập nhật các thông tin cơ bản
//             existingUser.setFirstName(userDetails.getFirstName());
//             existingUser.setSurname(userDetails.getSurname());
//             existingUser.setEmail(userDetails.getEmail());
//             existingUser.setShoppingPreference(userDetails.getShoppingPreference());
//             existingUser.setDobDay(userDetails.getDobDay());
//             existingUser.setDobMonth(userDetails.getDobMonth());
//             existingUser.setDobYear(userDetails.getDobYear());
//             existingUser.setEmailSignup(userDetails.isEmailSignup());
//             existingUser.setAgreeTerms(userDetails.isAgreeTerms());
//             existingUser.setRoleId(userDetails.getRoleId());

//             // Xử lý logic password: chỉ cập nhật nếu người dùng nhập pass mới
//             if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
//                 existingUser.setPassword(userDetails.getPassword());
//             }

//             User updatedUser = userRepository.save(existingUser);
//             return ResponseEntity.ok(updatedUser);
//         }).orElse(ResponseEntity.notFound().build());
//     }

//     // 5. Xóa người dùng (DELETE)
//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//         return userRepository.findById(id).map(user -> {
//             userRepository.delete(user);
//             return ResponseEntity.ok().body("Đã xóa thành công người dùng: " + id);
//         }).orElse(ResponseEntity.notFound().build());
//     }
// }