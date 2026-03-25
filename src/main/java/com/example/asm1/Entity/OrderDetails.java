package com.example.asm1.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Order_Details") // Trùng tên với bảng trong SQL
public class OrderDetails { 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // Thuộc đơn hàng nào

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // Mua sản phẩm nào

    private Integer quantity; // Số lượng
    private Double price; // Giá lúc mua

    // ... Khai báo biến xong thì paste đoạn này vào ...

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Product getProduct() {
        return product;
    }
 }
