package com.example.asm1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**")
                // 1. Ưu tiên tìm trong thư mục chứa ảnh vừa upload (trên ổ cứng)
                .addResourceLocations("file:src/main/resources/static/image/")
                // 2. Nếu không thấy, tìm tiếp trong thư mục resources gốc (để hiện logo, icon cũ)
                .addResourceLocations("classpath:/static/image/");
        
        // Bổ sung thêm dòng này để chắc chắn CSS/JS không bị ảnh hưởng (dự phòng)
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
                
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}