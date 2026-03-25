package com.example.asm1.controller;

import com.example.asm1.Entity.Order;
import com.example.asm1.repository.OrderRepository;
import com.example.asm1.service.MailService; // 1. Import thêm Service gửi mail
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MailService mailService; // 2. Tiêm MailService vào để sử dụng

    @GetMapping("")
    public String listOrders(Model model) {
        model.addAttribute("activePage", "orders");
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/order-management"; 
    }

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        model.addAttribute("order", order);
        return "admin/order-detail"; 
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("orderId") Long orderId, 
                               @RequestParam("newStatus") String newStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(newStatus);
        orderRepository.save(order);
        
        // 3. THÊM VÀO ĐÂY: Gửi email khi đơn hàng chuyển sang trạng thái "Shipping"
        // Em có thể tùy biến gửi mail ở các trạng thái khác (Delivered, Cancelled) nếu muốn
        if ("Shipping".equalsIgnoreCase(newStatus)) {
            try {
                mailService.sendOrderEmail(order);
            } catch (Exception e) {
                // Log lỗi ra console nếu gửi mail thất bại để không làm gián đoạn luồng web
                System.out.println("Lỗi gửi email xác nhận: " + e.getMessage());
            }
        }
        
        return "redirect:/admin/orders/detail/" + orderId;
    }
}