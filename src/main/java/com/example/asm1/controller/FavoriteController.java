// package com.example.asm1.controller;

// import com.example.asm1.Entity.Favorite;
// import com.example.asm1.Entity.Product;
// import com.example.asm1.Entity.User;
// import com.example.asm1.repository.FavoriteRepository;
// import com.example.asm1.repository.ProductRepository;
// import jakarta.servlet.http.HttpSession;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @Controller
// @RequestMapping("/favorites")
// public class FavoriteController {

//     @Autowired
//     private FavoriteRepository favoriteRepository;

//     @Autowired
//     private ProductRepository productRepository;

//     // ================= SHOW FAVORITES =================

//     @GetMapping
//     public String showFavorites(HttpSession session, Model model) {

//         User loggedInUser = (User) session.getAttribute("loggedInUser");

//         if (loggedInUser == null) {
//             return "redirect:/login";
//         }

//         List<Favorite> favorites = favoriteRepository.findByUser(loggedInUser);
//         model.addAttribute("favorites", favorites);

//         return "favorite"; // favorite.html
//     }

//     // ================= TOGGLE FAVORITE =================

//     @PostMapping("/toggle")
//     public String toggleFavorite(
//             @RequestParam("productId") Long productId,
//             HttpSession session) {

//         User loggedInUser = (User) session.getAttribute("loggedInUser");

//         if (loggedInUser == null) {
//             return "redirect:/login";
//         }

//         Favorite existingFavorite =
//                 favoriteRepository.findByUserAndProduct_Id(loggedInUser, productId);

//         if (existingFavorite != null) {
//             // ❌ Đã có → xóa
//             favoriteRepository.delete(existingFavorite);
//         } else {
//             // ✅ Chưa có → thêm
//             Product product = productRepository.findById(productId).orElse(null);

//             if (product != null) {
//                 Favorite favorite = new Favorite();
//                 favorite.setUser(loggedInUser);
//                 favorite.setProduct(product);
//                 favoriteRepository.save(favorite);
//             }
//         }

//         return "redirect:/home";
//     }
// }
