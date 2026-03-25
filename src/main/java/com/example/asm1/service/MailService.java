package com.example.asm1.service;

import com.example.asm1.Entity.Order; // ĐÃ SỬA: Import đúng Entity Order của em
import com.example.asm1.Entity.OrderDetails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final SecureRandom secureRandom = new SecureRandom();

    // 1. Gửi mã OTP đăng ký
    public String sendOtp(String toEmail) {
        String otp = String.valueOf(secureRandom.nextInt(899999) + 100000);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("phamkhoi2746@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Mã xác nhận đăng ký Nike Member");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;">
                    <h2 style="color: #111; text-align: center;">Xác thực tài khoản Nike Member</h2>
                    <p>Xin chào,</p>
                    <p>Cảm ơn bạn đã quan tâm đến sản phẩm của chúng tôi. Đây là mã xác thực (OTP) để hoàn tất đăng ký:</p>
                    <div style="background-color: #f4f4f4; padding: 15px; text-align: center; margin: 20px 0; border-radius: 5px;">
                        <span style="font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #fa5400;">%s</span>
                    </div>
                    <p>Mã này có hiệu lực trong vòng <strong>5 phút</strong>. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>
                    <hr style="border: none; border-top: 1px solid #eee;" />
                    <div style="margin-top: 20px; font-size: 13px; color: #666;">
                        <p>Trân trọng,</p>
                        <p><strong>Đội ngũ Hỗ trợ Nike Việt Nam</strong></p>
                    </div>
                </div>
                """.formatted(otp);

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
        return otp;
    }

    // 2. Gửi Email xác nhận đơn hàng (Bản nâng cấp chống lỗi)
    public void sendOrderEmail(Order order) {
        System.out.println(">>> ĐANG BẮT ĐẦU LUỒNG GỬI MAIL CHO ĐƠN: #" + order.getId());
        try {
            // Kiểm tra email khách có tồn tại không
            if (order.getUser() == null || order.getUser().getEmail() == null) {
                System.err.println(">>> LỖI: Email của khách hàng bị trống, không thể gửi!");
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("phamkhoi2746@gmail.com");
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Xác nhận đơn hàng Nike Store #" + order.getId());

            StringBuilder productRows = new StringBuilder();
            // Đề phòng trường hợp danh sách sản phẩm rỗng
            if (order.getOrderDetails() != null) {
                for (OrderDetails item : order.getOrderDetails()) {
                    productRows.append(String.format("""
                        <tr>
                            <td style="padding: 10px; border-bottom: 1px solid #eee;">%s</td>
                            <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: center;">%d</td>
                            <td style="padding: 10px; border-bottom: 1px solid #eee; text-align: right;">%,.0f₫</td>
                        </tr>
                    """, item.getProduct().getName(), item.getQuantity(), item.getPrice() * item.getQuantity()));
                }
            }

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;">
                    <div style="text-align: center; margin-bottom: 20px;">
                        <h1 style="color: #111; margin: 0;">NIKE STORE</h1>
                        <p style="color: #757575;">Cảm ơn bạn đã đặt hàng!</p>
                    </div>
                    <h3 style="border-bottom: 2px solid #111; padding-bottom: 10px;">Thông tin đơn hàng #%d</h3>
                    <p><strong>Trạng thái:</strong> %s</p>
                    <p><strong>Địa chỉ giao hàng:</strong> %s</p>
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;">
                        <thead>
                            <tr style="background-color: #f4f4f4;">
                                <th style="padding: 10px; text-align: left;">Sản phẩm</th>
                                <th style="padding: 10px; text-align: center;">SL</th>
                                <th style="padding: 10px; text-align: right;">Tổng</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>
                    <div style="text-align: right; font-size: 18px; font-weight: bold; margin-top: 20px;">
                        Tổng cộng: <span style="color: #fa5400;">%,.0f₫</span>
                    </div>
                    <div style="font-size: 12px; color: #666; text-align: center; margin-top: 30px;">
                        <p>Nike Store Việt Nam | Hotline: 1900 xxxx</p>
                    </div>
                </div>
                """.formatted(
                    order.getId(), 
                    order.getStatus(), 
                    (order.getAddress() != null ? order.getAddress() : "Chưa có địa chỉ"), 
                    productRows.toString(), 
                    order.getTotalAmount()
                );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println(">>> CHÚC MỪNG: Mail đã gửi thành công tới " + order.getUser().getEmail());

        } catch (Exception e) {
            System.err.println(">>> LỖI NGHIÊM TRỌNG KHI GỬI MAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}