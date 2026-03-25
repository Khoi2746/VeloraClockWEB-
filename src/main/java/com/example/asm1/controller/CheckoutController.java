package com.example.asm1.controller;

import com.example.asm1.Entity.*;
import com.example.asm1.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    // 1. HIỂN THỊ TRANG CHECKOUT (GET)
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session) {
        // Kiểm tra login
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/login";

        // Lấy giỏ hàng
        List<CartItem> cartItems = cartItemRepository.findByUserId(currentUser.getId());
        if (cartItems.isEmpty()) return "redirect:/cart";

        // Tính tổng tiền
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getProduct().getPrice() * item.getQuantity();
        }

        // Tạo form rỗng để điền
        OrderForm orderForm = new OrderForm();
        // Tự điền thông tin User vào form cho tiện (nếu có)
        orderForm.setFirstName(currentUser.getFirstName());
        orderForm.setLastName(currentUser.getSurname());
        orderForm.setEmail(currentUser.getEmail());

        // Đẩy dữ liệu sang HTML
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", subtotal); // Giả sử Free ship
        model.addAttribute("orderForm", orderForm); // QUAN TRỌNG: Đẩy form object sang

        return "checkout"; // Trỏ đến file checkout.html của em
    }

    // 2. XỬ LÝ KHI BẤM NÚT PLACE ORDER (POST)
    @PostMapping("/place-order")
    public String placeOrder(@ModelAttribute("orderForm") OrderForm orderForm, 
                             HttpSession session) {
        
        User currentUser = (User) session.getAttribute("loggedInUser");
        if (currentUser == null) return "redirect:/login";

        List<CartItem> cartItems = cartItemRepository.findByUserId(currentUser.getId());
        if (cartItems.isEmpty()) return "redirect:/cart";

        // --- LƯU ORDER VÀO DB ---
        Order newOrder = new Order();
        newOrder.setUser(currentUser);
        newOrder.setOrderDate(new Date());
        newOrder.setStatus("Pending");
        
        // Lấy dữ liệu từ FORM người dùng nhập gán vào ORDER
        newOrder.setAddress(orderForm.getAddress());
        newOrder.setPhoneNumber(orderForm.getPhoneNumber());
        // (Nếu em muốn lưu cả tên người nhận thì cần thêm cột vào bảng Orders, 
        // hiện tại anh chỉ lưu địa chỉ và sđt theo entity cũ của em)

        // Tính tổng tiền lại (để bảo mật, ko lấy từ form)
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        newOrder.setTotalAmount(total);

        // Lưu Order Head
        Order savedOrder = orderRepository.save(newOrder);

        // Lưu Chi tiết (Order Details)
        for (CartItem item : cartItems) {
            OrderDetails detail = new OrderDetails();
            detail.setOrder(savedOrder);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            orderDetailRepository.save(detail);
        }

        // Xóa giỏ hàng
        cartItemRepository.deleteAll(cartItems);

        return "checkout-success";
    }
}