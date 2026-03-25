package com.example.asm1.controller;

import com.example.asm1.Entity.Product;
import com.example.asm1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Thêm cái này

import java.nio.file.*;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductRepository productRepository;

    // 1. Hiển thị danh sách & Form rỗng
    @GetMapping("")
    public String listProducts(Model model) {
        model.addAttribute("activePage", "products");
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("product", new Product()); 
        return "admin/product-management";
    }

    // 2. XỬ LÝ LƯU (CẬP NHẬT ĐỂ HỨNG FILE ẢNH)
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam("imageFile") MultipartFile imageFile) {
        
        // --- LOGIC XỬ LÝ FILE ẢNH ---
        if (!imageFile.isEmpty()) {
            try {
                // Xác định nơi lưu (src/main/resources/static/image/)
                String uploadDir = "src/main/resources/static/image/";
                
                // Tạo tên file duy nhất bằng UUID để không bị trùng ảnh
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get(uploadDir + fileName);

                // Tạo thư mục nếu chưa có
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }

                // Copy file từ máy khách vào thư mục dự án
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Lưu đường dẫn nội bộ vào DB (VD: /image/abc.png)
                product.setImageUrl("/image/" + fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        // Nếu không chọn file mới (imageFile.isEmpty()) 
        // thì product.imageUrl sẽ giữ giá trị cũ nhờ cái input hidden trong form của em.

        productRepository.save(product);
        return "redirect:/admin/products";
    }

    // 3. Xử lý khi bấm nút Edit
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model) {
        model.addAttribute("activePage", "products");
        Product p = productRepository.findById(id).orElse(new Product());
        model.addAttribute("product", p); 
        model.addAttribute("products", productRepository.findAll()); 
        return "admin/product-management";
    }

    // 4. Xử lý Xóa
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }
}