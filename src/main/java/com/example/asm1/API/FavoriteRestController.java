package com.example.asm1.API;

import com.example.asm1.Entity.Favorite;
import com.example.asm1.Entity.Product;
import com.example.asm1.Entity.User;
import com.example.asm1.repository.FavoriteRepository;
import com.example.asm1.repository.ProductRepository;
import com.example.asm1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteRestController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Lấy danh sách sản phẩm yêu thích của một User
    @GetMapping("/{userId}")
    public ResponseEntity<?> getFavorites(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User không tồn tại");
        }
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return ResponseEntity.ok(favorites);
    }

    // 2. Toggle Favorite: Nhấn một lần là Thích, nhấn lần nữa là Bỏ thích
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(@RequestBody Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        Long productId = Long.valueOf(payload.get("productId").toString());

        User user = userRepository.findById(userId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (user == null || product == null) {
            return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ");
        }

        // Kiểm tra xem đã thích sản phẩm này chưa
        List<Favorite> existList = favoriteRepository.findByUserAndProduct_Id(user, productId);

        if (!existList.isEmpty()) {
            // Nếu đã có trong danh sách -> Xóa (Bỏ thích)
            favoriteRepository.deleteAll(existList);
            return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích");
        } else {
            // Nếu chưa có -> Thêm mới (Thích)
            Favorite newFav = new Favorite();
            newFav.setUser(user);
            newFav.setProduct(product);
            return ResponseEntity.ok(favoriteRepository.save(newFav));
        }
    }

    // 3. Xóa một mục yêu thích cụ thể theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long id) {
        return favoriteRepository.findById(id).map(fav -> {
            favoriteRepository.delete(fav);
            return ResponseEntity.ok("Đã xóa thành công");
        }).orElse(ResponseEntity.notFound().build());
    }
}