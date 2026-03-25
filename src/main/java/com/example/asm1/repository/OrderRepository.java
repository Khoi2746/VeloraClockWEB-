package com.example.asm1.repository;

import com.example.asm1.Entity.Order;
import com.example.asm1.Entity.User;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findTop5ByOrderByOrderDateDesc();

    // 1. Tính tổng doanh thu
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != 'Cancelled'")
    Double getTotalRevenue();

    // 2. Thống kê doanh thu theo tháng (Dùng CURRENT_DATE để tương thích HQL)
    @Query("SELECT MONTH(o.orderDate), SUM(o.totalAmount) " +
           "FROM Order o " +
           "WHERE YEAR(o.orderDate) = YEAR(CURRENT_DATE) " +
           "GROUP BY MONTH(o.orderDate) " +
           "ORDER BY MONTH(o.orderDate)")
    List<Object[]> getRevenueByMonth();

    // 3. QUAN TRỌNG: Đã sửa OrderDetail thành OrderDetails (có chữ 's')
    @Query("SELECT p.category, SUM(od.quantity) " +
           "FROM OrderDetails od " + // Sửa chỗ này cho khớp với Entity của em
           "JOIN od.product p " +
           "GROUP BY p.category")
    List<Object[]> getSalesByCategory();

    List<Order> findByUserOrderByOrderDateDesc(User user);
}