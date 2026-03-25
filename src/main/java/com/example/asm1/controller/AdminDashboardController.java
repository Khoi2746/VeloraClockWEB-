package com.example.asm1.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// import javax.servlet.http.HttpSession; // Nếu dùng Spring Boot 2 cũ
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.asm1.Entity.Order;
import com.example.asm1.repository.OrderRepository;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    // 1. TIÊM REPOSITORY VÀO (Đây là đối tượng thực tế để gọi hàm)
    @Autowired
    private OrderRepository orderRepository; 

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        
        // 2. SỬA CHỖ NÀY: Gọi bằng 'orderRepository' (chữ o thường), KHÔNG gọi bằng 'OrderRepository'
        List<Order> latestOrders = orderRepository.findTop5ByOrderByOrderDateDesc();
        
        List<String> activities = new ArrayList<>();

        for (Order o : latestOrders) {
            // Nhờ file Order.java đã sửa Getter ở bước trước, dòng này sẽ hết lỗi
            activities.add("New Order #" + o.getId() + " from " + o.getUser().getFirstName() + " - " + o.getTotalAmount() + "₫");
        }

        model.addAttribute("recentActivities", activities);
        return "admin/Main";
    }
}