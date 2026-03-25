package com.example.asm1.controller;

import com.example.asm1.Entity.Order;
import com.example.asm1.Entity.User;
import com.example.asm1.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerOrderController {

    @Autowired
    private OrderRepository orderRepository;

    // 1. Xem danh sách lịch sử đơn hàng
    @GetMapping("/orders")
    public String viewOrderHistory(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Order> myOrders = orderRepository.findByUserOrderByOrderDateDesc(loggedInUser);
        
        model.addAttribute("orders", myOrders);
        
        return "user/order-history"; 
    }

    // 2. Xem chi tiết & Bản đồ (Tracking)
    @GetMapping("/track/{id}")
    public String trackOrder(@PathVariable Long id, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        @SuppressWarnings("null")
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));

        if (!order.getUser().getId().equals(loggedInUser.getId())) {
            return "redirect:/home";
        }

        model.addAttribute("order", order);
        return "user/order-tracking"; 
    }
}