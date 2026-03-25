package com.example.asm1.Entity; // Hoặc để trong controller tạm cũng được

import lombok.Data;

@Data
public class OrderForm {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
}