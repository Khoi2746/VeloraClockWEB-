package com.example.asm1.controller;

import com.example.asm1.Entity.CartItem;
import com.example.asm1.Entity.Favorite;
import com.example.asm1.Entity.Product;
import com.example.asm1.Entity.User;
import com.example.asm1.repository.CartItemRepository;
import com.example.asm1.repository.FavoriteRepository;
import com.example.asm1.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Thêm import này
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ShoppingController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    // =========================================================
    // 1. HIỂN THỊ GIỎ HÀNG & TÍNH TIỀN
    // =========================================================
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // Lấy danh sách sản phẩm trong giỏ của User này
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        // Tính tổng tiền
        double totalAmount = 0;
        for (CartItem item : cartItems) {
            if (item.getProduct() != null && item.getProduct().getPrice() != null) {
                totalAmount += item.getProduct().getPrice() * item.getQuantity();
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", totalAmount);
        model.addAttribute("total", totalAmount);

        return "cart";
    }

    // =========================================================
    // 2. XỬ LÝ TĂNG / GIẢM SỐ LƯỢNG (MỚI THÊM VÀO ĐÂY)
    // =========================================================
    @GetMapping("/cart/update/{id}/{action}")
    public String updateQuantity(@PathVariable("id") Long cartItemId,
            @PathVariable("action") String action,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/login";

        // Tìm dòng trong giỏ hàng cần sửa
        CartItem item = cartItemRepository.findById(cartItemId).orElse(null);

        // Chỉ cho phép sửa nếu item tồn tại và thuộc về User đang đăng nhập (bảo mật)
        if (item != null && item.getUser().getId().equals(user.getId())) {

            // Logic TĂNG
            if ("increase".equals(action)) {
                item.setQuantity(item.getQuantity() + 1);
                cartItemRepository.save(item);
            }
            // Logic GIẢM
            else if ("decrease".equals(action)) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                    cartItemRepository.save(item);
                }
                // Nếu đang là 1 mà bấm giảm thì giữ nguyên (hoặc em có thể gọi delete để xóa
                // luôn)
            }
        }

        // Load lại trang giỏ hàng để cập nhật giá tiền
        return "redirect:/cart";
    }

    // =========================================================
    // 3. THÊM VÀO GIỎ HÀNG (Logic cũ giữ nguyên)
    // =========================================================
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/login";

        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            CartItem existingItem = cartItemRepository.findByUserAndProduct(user, product);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                cartItemRepository.save(existingItem);
            } else {
                CartItem newItem = new CartItem();
                newItem.setUser(user);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                cartItemRepository.save(newItem);
            }
        }
        return "redirect:/cart";
    }

    // =========================================================
    // 4. XÓA KHỎI GIỎ HÀNG
    // =========================================================
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("cartItemId") Long cartItemId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/login";

        cartItemRepository.findById(cartItemId).ifPresent(item -> {
            if (item.getUser().getId().equals(user.getId())) {
                cartItemRepository.delete(item);
            }
        });
        return "redirect:/cart";
    }

    // =========================================================
    // 5. CÁC CHỨC NĂNG KHÁC (Checkout, Favorite...)
    // =========================================================

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/login";

        // Logic checkout: Tạo đơn hàng (Order) -> Lưu OrderDetails -> Xóa giỏ hàng
        // Tạm thời em đang để xóa giỏ hàng
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());
        cartItemRepository.deleteAll(items);

        return "redirect:/home";
    }

    @GetMapping("/favorites")
    public String viewFavorites(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/login";

        List<Favorite> favorites = favoriteRepository.findByUser(user);
        model.addAttribute("favorites", favorites);
        return "favorite";
    }

    @PostMapping("/favorites/toggle")
    public String toggleFavorite(@RequestParam("productId") Long productId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/login";

        List<Favorite> existList = favoriteRepository.findByUserAndProduct_Id(user, productId);
        if (!existList.isEmpty()) {
            favoriteRepository.deleteAll(existList);
        } else {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                Favorite newFav = new Favorite();
                newFav.setUser(user);
                newFav.setProduct(product);
                favoriteRepository.save(newFav);
            }
        }
        return "redirect:/favorites";
    }
}