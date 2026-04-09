package com.example.asm1.API;

import com.example.asm1.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsRestController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<?> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Lấy con số tổng quát
        Double totalRevenue = orderRepository.getTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0);
        stats.put("totalOrders", orderRepository.count());

        // 2. Dữ liệu doanh thu theo tháng (Line Chart)
        stats.put("revenueByMonth", orderRepository.getRevenueByMonth());

        // 3. Dữ liệu doanh số theo danh mục (Doughnut Chart)
        stats.put("salesByCategory", orderRepository.getSalesByCategory());

        return ResponseEntity.ok(stats);
    }
}