package com.example.asm1.API;

import com.example.asm1.Entity.CartItem;
import com.example.asm1.Entity.Product;
import com.example.asm1.Entity.User;
import com.example.asm1.repository.CartItemRepository;
import com.example.asm1.repository.ProductRepository;
import com.example.asm1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*") // Để VueJS npm run dev gọi vào được
@RestController
@RequestMapping("/api/cart")
public class CartRestController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Lấy danh sách giỏ hàng theo User ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return ResponseEntity.ok(items);
    }

    // 2. Thêm sản phẩm vào giỏ
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        Long productId = Long.valueOf(payload.get("productId").toString());
        int quantity = Integer.parseInt(payload.get("quantity").toString());

        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null || product == null) {
            return ResponseEntity.badRequest().body("User hoặc Product không tồn tại");
        }

        CartItem existingItem = cartItemRepository.findByUserAndProduct(user, product);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return ResponseEntity.ok(cartItemRepository.save(existingItem));
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            return ResponseEntity.ok(cartItemRepository.save(newItem));
        }
    }

    // 3. Cập nhật số lượng (Tăng/Giảm)
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long cartItemId, @RequestParam String action) {
        return cartItemRepository.findById(cartItemId).map(item -> {
            if ("increase".equals(action)) {
                item.setQuantity(item.getQuantity() + 1);
            } else if ("decrease".equals(action)) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                }
            }
            return ResponseEntity.ok(cartItemRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Xóa một item khỏi giỏ
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId) {
        return cartItemRepository.findById(cartItemId).map(item -> {
            cartItemRepository.delete(item);
            return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Xóa sạch giỏ hàng (sau khi Checkout thành công)
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(items);
        return ResponseEntity.ok("Giỏ hàng đã được làm trống");
    }
}
