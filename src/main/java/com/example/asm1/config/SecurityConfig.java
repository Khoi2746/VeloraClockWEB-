package com.example.asm1.config;

import com.example.asm1.Entity.User;
import com.example.asm1.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Tắt CSRF (Để các form cũ không lỗi)
            .csrf(csrf -> csrf.disable())

            // 2. Cấp quyền (Thả cửa vì đã có AdminInterceptor)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // 3. Cấu hình Đăng nhập OAuth2 (Google + Facebook)
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login") 
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request,
                                                        HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {

                        // A. Lấy thông tin User từ Google/Facebook trả về
                        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                        
                        // Lấy tên nhà cung cấp (google hoặc facebook)
                        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                        String clientName = oauthToken.getAuthorizedClientRegistrationId();

                        String email = oauth2User.getAttribute("email");
                        String name = oauth2User.getAttribute("name");
                        String id = oauth2User.getName(); // Lấy ID định danh duy nhất

                        // B. XỬ LÝ QUAN TRỌNG: Nếu Email bị Null (Thường gặp ở Facebook)
                        if (email == null || email.isEmpty()) {
                            // Tự chế ra email giả để không bị lỗi Database
                            // Ví dụ: 1002394@facebook.com
                            email = id + "@" + clientName + ".com";
                        }

                        // C. Nếu tên bị Null (Ít gặp nhưng cứ phòng hờ)
                        if (name == null || name.isEmpty()) {
                            name = "User " + clientName;
                        }

                        // D. Kiểm tra Email trong Database
                        User user = userRepository.findByEmail(email);

                        if (user == null) {
                            // E. Nếu chưa có -> Tạo tài khoản mới
                            user = new User();
                            user.setEmail(email);
                            user.setFirstName(name);
                            user.setPassword("OAUTH2_LOGIN"); // Mật khẩu giả
                            user.setRoleId(2); // Role 2: Khách hàng (Lưu ý: số 2, không phải 2L)
                            
                            // Lưu vào DB
                            userRepository.save(user);
                        }

                        // F. Lưu User vào Session (Để code cũ ShoppingController nhận diện)
                        HttpSession session = request.getSession();
                        session.setAttribute("loggedInUser", user);

                        // G. Chuyển hướng về trang chủ
                        response.sendRedirect("/home");
                    }
                })
            );

        return http.build();
    }
}