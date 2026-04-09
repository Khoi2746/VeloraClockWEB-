package com.example.asm1.API;

import com.example.asm1.Entity.Order;
import com.example.asm1.Entity.User;
import com.example.asm1.repository.OrderRepository;
import com.example.asm1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Lấy toàn bộ lịch sử đơn hàng của một User (Dùng cho VueJS trang History)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User không tồn tại");
        }
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        return ResponseEntity.ok(orders);
    }

    // 2. Xem chi tiết 1 đơn hàng cụ thể (Dùng cho VueJS trang Tracking)
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId) {
        return orderRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Admin cập nhật trạng thái đơn hàng (Ví dụ: Đang giao, Đã giao)
    // Dùng PutMapping vì đây là hành động chỉnh sửa dữ liệu đã có
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId, @RequestParam String status) {
        return orderRepository.findById(orderId).map(order -> {
            order.setStatus(status);
            return ResponseEntity.ok(orderRepository.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Hủy đơn hàng (Dành cho User nếu đơn hàng chưa giao)
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            if ("Pending".equals(order.getStatus())) { // Chỉ cho hủy nếu đang chờ duyệt
                order.setStatus("Cancelled");
                return ResponseEntity.ok(orderRepository.save(order));
            }
            return ResponseEntity.badRequest().body("Không thể hủy đơn hàng đã xử lý");
        }).orElse(ResponseEntity.notFound().build());
    }
}