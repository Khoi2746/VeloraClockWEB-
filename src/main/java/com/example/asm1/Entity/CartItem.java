package com.example.asm1.Entity;

import jakarta.persistence.*;
import lombok.Data; // Nếu không dùng Lombok thì tự generate Getter/Setter nhé

@Entity
@Table(name = "cart_items")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Khách hàng

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // Sản phẩm

    private int quantity; // Số lượng mua
}