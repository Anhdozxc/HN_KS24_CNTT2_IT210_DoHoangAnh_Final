package com.cinema.config;

import com.cinema.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // Chi bao ve nhung duong dan nay
                .addPathPatterns(
                        "/admin/**",   // Trang admin
                        "/staff/**",   // Trang nhan vien
                        "/booking/**", // Dat ve
                        "/profile/**"  // Ho so ca nhan
                );
        // Khong bao ve: /auth/**, /css/**, /js/**, / (trang chu), /movie/**
    }
}