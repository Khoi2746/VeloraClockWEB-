package com.example.asm1.Entity; // Nhớ sửa package cho đúng chỗ em đặt

import lombok.Data; // Nếu dùng Lombok, không thì tự viết Getter/Setter

@Data
public class CheckOutForm {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private String note;
}