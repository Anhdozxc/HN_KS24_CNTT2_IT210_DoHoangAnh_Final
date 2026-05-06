package com.cinema.interceptor;

import com.cinema.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // Lay user tu session
        User user = (session != null) ? (User) session.getAttribute("loggedUser") : null;

        // Chua dang nhap -> chuyen ve trang login
        if (user == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        String role = user.getRole().name();

        // CORE-02: Khach hang khong vao duoc trang admin
        if (path.startsWith("/admin") && !role.equals("ADMIN")) {
            response.sendRedirect("/?error=access_denied");
            return false;
        }

        // CORE-02: Khach hang khong vao duoc trang staff
        if (path.startsWith("/staff") && role.equals("CUSTOMER")) {
            response.sendRedirect("/?error=access_denied");
            return false;
        }

        return true;  // Cho di tiep
    }
}