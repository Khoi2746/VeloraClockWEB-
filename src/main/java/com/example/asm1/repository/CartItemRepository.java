package com.example.asm1.repository;

import com.example.asm1.Entity.CartItem;
import com.example.asm1.Entity.Product;
import com.example.asm1.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 1. Lấy toàn bộ danh sách giỏ hàng của một User (để hiển thị trang Cart)
    List<CartItem> findByUser(User user);

    // 2. Tìm xem User đã có sản phẩm này trong giỏ chưa (để cộng dồn số lượng thay vì tạo mới)
    // Cách 1: Truyền vào object Product (Khuyên dùng, code gọn hơn)
    CartItem findByUserAndProduct(User user, Product product);
    
    // Cách 2: Nếu em thích truyền ID như bên Favorite thì dùng dòng này (chọn 1 trong 2 thôi nhé)
    // CartItem findByUserAndProduct_Id(User user, Long productId);
    
    // 3. Xóa các sản phẩm trong giỏ hàng của User (Dùng khi thanh toán xong)
    void deleteByUser(User user);

    List<CartItem> findByUserId(Long userId);

}