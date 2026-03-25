package com.example.asm1.controller;

import com.example.asm1.Entity.Product;
import com.example.asm1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // =========================
    // 1. Trang New Arrivals (mặc định)
    // =========================
    @GetMapping({"/", "/new-arrivals"})
    public String showNewArrivals(Model model) {

        List<Product> products = productRepository.findAll(
                Sort.by("createdAt").descending()
        );

        model.addAttribute("products", products);
        model.addAttribute("totalItems", products.size());

        return "NewArrival";
    }

    // =========================
    // 2. Trang chi tiết sản phẩm
    // =========================
    @GetMapping("/product/detail/{id}")
    public String showProductDetail(
            @PathVariable("id") Long id,
            Model model
    ) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return "redirect:/new-arrivals";
        }

        model.addAttribute("product", product);
        return "ProductDetail";
    }

    // =========================
    // 3. Tìm kiếm sản phẩm theo tên
    // =========================
    @GetMapping("/search")
    public String searchProduct(
            @RequestParam("keyword") String keyword,
            Model model
    ) {

        List<Product> products =
                productRepository.findByNameContainingIgnoreCase(keyword);

        model.addAttribute("products", products);
        model.addAttribute("totalItems", products.size());
        model.addAttribute("searchKeyword", keyword);

        return "NewArrival";
    }

    // =========================
    // 4. Filter + Sort sản phẩm
    // =========================
   @GetMapping("/products")
    public String filterAndSortProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String sort,
            Model model
    ) {

        // 1. Xác định tiêu chí sắp xếp (Sort)
        Sort sortOrder = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "price_asc":
                    sortOrder = Sort.by("price").ascending();
                    break;
                case "price_desc":
                    sortOrder = Sort.by("price").descending();
                    break;
                case "newest":
                    sortOrder = Sort.by("createdAt").descending();
                    break;
            }
        }

        // 2. Xử lý chuỗi rỗng ("") thành null để database dễ hiểu
        String filterCategory = (category != null && !category.trim().isEmpty()) ? category : null;
        String filterGender = (gender != null && !gender.trim().isEmpty()) ? gender : null;

        // 3. Gọi DB vừa lọc vừa sắp xếp
        List<Product> products = productRepository.filterAndSortProducts(filterCategory, filterGender, sortOrder);

        // 4. Đẩy data ra View
        model.addAttribute("products", products);
        model.addAttribute("totalItems", products.size());
        
        // Giữ lại trạng thái người dùng đã chọn để hiển thị trên giao diện
        model.addAttribute("category", category);
        model.addAttribute("gender", gender);
        model.addAttribute("sort", sort);

        return "NewArrival";
    }
    
}
