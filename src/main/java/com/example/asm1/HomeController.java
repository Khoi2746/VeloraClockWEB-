package com.example.asm1;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
// import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.asm1.Entity.Product;
import com.example.asm1.repository.ProductRepository;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/home")
    public String getHomePage(Model model) {
        // Lấy tất cả sản phẩm từ DB
        List<Product> productList = productRepository.findAll();
        
        // Gửi danh sách này sang Thymeleaf với tên là "products"
        model.addAttribute("products", productList);
        
        return "index";
    }
}