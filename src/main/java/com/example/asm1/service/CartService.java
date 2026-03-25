package com.example.asm1.service;

import com.example.asm1.Entity.CartItem;
import com.example.asm1.Entity.Product;
import com.example.asm1.Entity.User;
import com.example.asm1.repository.CartItemRepository;
import com.example.asm1.repository.ProductRepository; // Nhớ import cái này để tìm sản phẩm
import jakarta.transaction.Transactional; // Quan trọng để xóa được dữ liệu
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    // 1. Lấy danh sách giỏ hàng của User
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    // 2. Tính tổng tiền (để hiển thị bên Checkout)
   public long getCartTotal(User user) {
    List<CartItem> items = cartItemRepository.findByUser(user);
    return items.stream()
            // Ép kiểu (long) vào trước phép tính để chắc chắn nó trả về số nguyên
            .mapToLong(item -> (long) (item.getProduct().getPrice() * item.getQuantity()))
            .sum();
}

    // 3. THÊM VÀO GIỎ (Logic quan trọng nhất)
    public void addToCart(User user, Long productId, int quantity) {
        // Tìm sản phẩm trong DB xem có tồn tại không
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra xem User này đã có món này trong giỏ chưa (Dùng hàm em vừa viết)
        CartItem existingItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingItem != null) {
            // TRƯỜNG HỢP 1: Đã có -> Cộng dồn số lượng
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            // TRƯỜNG HỢP 2: Chưa có -> Tạo mới
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            // newItem.setSize("M"); // Nếu muốn set size mặc định hoặc truyền vào từ tham số
            cartItemRepository.save(newItem);
        }
    }

    // 4. Xóa 1 món (Nút thùng rác)
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // 5. Xóa sạch giỏ hàng (Dùng khi Checkout xong)
    // Lưu ý: Phải có @Transactional thì hàm deleteByUser mới chạy được
    @Transactional 
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}