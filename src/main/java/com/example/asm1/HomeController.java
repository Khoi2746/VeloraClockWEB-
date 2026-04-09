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


// package com.example.asm1;

// import java.util.List;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController; 

// import com.example.asm1.Entity.Product;
// import com.example.asm1.repository.ProductRepository;

// @RestController
// @RequestMapping("/home")
// public class HomeController {

//     @Autowired
//     private ProductRepository productRepository;

//     // Lấy danh sách tất cả sản phẩm
//     @GetMapping
//     public List<Product> getAll() {
//         return productRepository.findAll();
//     }

//     // Thêm mới sản phẩm
//     @PostMapping
//     public Product create(@RequestBody Product product) {
//         return productRepository.save(product);
//     }

//     // Cập nhật sản phẩm theo ID (Dùng kiểu Long để khớp với Entity)
//     @PutMapping("/{id}")
//     public Product update(@PathVariable("id") Long id, @RequestBody Product product) {
//         if (productRepository.existsById(id)) {
//             product.setId(id); // Giữ nguyên ID từ URL
//             return productRepository.save(product);
//         }
//         return null;
//     }

//     // Xóa sản phẩm theo ID (Dùng kiểu Long để khớp với Entity)
//     @DeleteMapping("/{id}")
//     public void delete(@PathVariable("id") Long id) {
//         productRepository.deleteById(id);
//     }
// }