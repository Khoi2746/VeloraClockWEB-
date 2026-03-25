package com.example.asm1.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.asm1.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException; // ĐÃ SỬA: Import đúng thư viện
import com.fasterxml.jackson.databind.ObjectMapper; // ĐÃ SỬA: Import đúng thư viện

@Controller
@RequestMapping("/admin")
public class AdminStatisticsController {

    @Autowired
    private OrderRepository orderRepository; // ĐÃ THÊM: Tiêm repository vào để sử dụng

    @GetMapping("/statistics")
    public String getStatistics(Model model) throws JsonProcessingException {
        // 1. Lấy các con số tổng quát (Dùng biến orderRepository viết thường)
        Double totalRevenue = orderRepository.getTotalRevenue();
        long totalOrders = orderRepository.count();
        
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0);
        model.addAttribute("totalOrders", totalOrders);

        // 2. Xử lý dữ liệu biểu đồ Doanh thu (Line Chart)
        List<Object[]> revenueData = orderRepository.getRevenueByMonth();
        List<String> months = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();
        if (revenueData != null) {
            for (Object[] row : revenueData) {
                months.add("Tháng " + row[0]);
                amounts.add((Double) row[1]);
            }
        }

        // 3. Xử lý dữ liệu biểu đồ Danh mục (Doughnut Chart)
        List<Object[]> categoryData = orderRepository.getSalesByCategory();
        List<String> catNames = new ArrayList<>();
        List<Long> catCounts = new ArrayList<>();
        if (categoryData != null) {
            for (Object[] row : categoryData) {
                catNames.add((String) row[0]);
                catCounts.add((Long) row[1]);
            }
        }

        // Chuyển dữ liệu sang JSON để JavaScript nhận diện được
        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute("monthsJson", mapper.writeValueAsString(months));
        model.addAttribute("amountsJson", mapper.writeValueAsString(amounts));
        model.addAttribute("catNamesJson", mapper.writeValueAsString(catNames));
        model.addAttribute("catCountsJson", mapper.writeValueAsString(catCounts));

        return "admin/statistics";
    }
}