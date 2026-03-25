package com.example.asm1.Entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data; // Nếu không dùng Lombok thì tự viết Getter/Setter nhé

@Data
public class MailForm {

    @NotBlank(message = "Please enter your email.") // Không được để trống
    @Email(message = "Invalid email address.")      // Phải đúng định dạng email
    private String email;
}