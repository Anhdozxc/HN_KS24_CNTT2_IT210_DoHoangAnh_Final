package com.cinema.controller;

import com.cinema.dto.RegisterDTO;
import com.cinema.entity.User;
import com.cinema.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.cinema.dto.LoginDTO;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired private UserService userService;

    // Hiển thi trang đăng nhập
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("dto", new LoginDTO());
        return "auth/login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("dto") LoginDTO dto,
                        BindingResult result,
                        HttpSession session,
                        Model model) {
        if (result.hasErrors()) {
            return "auth/login";
        }

        return userService.login(dto.getUsername(), dto.getPassword())
                .map(user -> {
                    session.setAttribute("loggedUser", user);
                    return switch (user.getRole()) {
                        case ADMIN -> "redirect:/admin/dashboard";
                        case STAFF -> "redirect:/staff/orders";
                        default    -> "redirect:/";
                    };
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
                    return "auth/login";
                });
    }

    // Hiển thi trang đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("dto", new RegisterDTO());
        return "auth/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("dto") RegisterDTO dto,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "auth/register";  // Trả về form nếu có lỗi validation
        }
        try {
            userService.register(dto);
            return "redirect:/auth/login?success=registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // Đăng xuất
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Xóa session
        return "redirect:/auth/login?success=logout";
    }
}