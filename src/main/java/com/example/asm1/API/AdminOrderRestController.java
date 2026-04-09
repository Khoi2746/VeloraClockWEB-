package com.example.asm1.API;

import com.example.asm1.Entity.Order;
import com.example.asm1.repository.OrderRepository;
import com.example.asm1.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderRestController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MailService mailService;

    // 1. Lấy toàn bộ đơn hàng (Cho trang quản lý của Admin)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 2. Lấy chi tiết đơn hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Cập nhật trạng thái đơn hàng & Gửi Mail
    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestBody Map<String, Object> payload) {
        try {
            Long orderId = Long.valueOf(payload.get("orderId").toString());
            String newStatus = payload.get("newStatus").toString();

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));

            order.setStatus(newStatus);
            orderRepository.save(order);

            // Logic gửi mail giống hệt bên Controller cũ của ku em
            if ("Shipping".equalsIgnoreCase(newStatus)) {
                try {
                    mailService.sendOrderEmail(order);
                } catch (Exception e) {
                    // Trả về thông báo kèm cảnh báo gửi mail lỗi nhưng vẫn lưu được status
                    return ResponseEntity
                            .ok("Cập nhật trạng thái thành công nhưng gửi mail thất bại: " + e.getMessage());
                }
            }

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xử lý: " + e.getMessage());
        }
    }
}