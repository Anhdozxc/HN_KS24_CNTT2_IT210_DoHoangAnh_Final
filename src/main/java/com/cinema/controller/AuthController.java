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

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired private UserService userService;

    // Hien thi trang dang nhap
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    // Xu ly dang nhap
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        return userService.login(username, password)
                .map(user -> {
                    // Luu user vao session
                    session.setAttribute("loggedUser", user);
                    // Chuyen huong theo quyen
                    return switch (user.getRole()) {
                        case ADMIN -> "redirect:/admin/dashboard";
                        case STAFF -> "redirect:/staff/orders";
                        default    -> "redirect:/";
                    };
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Sai ten dang nhap hoac mat khau");
                    return "auth/login";
                });
    }

    // Hien thi trang dang ky
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("dto", new RegisterDTO());
        return "auth/register";
    }

    // Xu ly dang ky
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("dto") RegisterDTO dto,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "auth/register";  // Tra ve form neu co loi validation
        }
        try {
            userService.register(dto);
            return "redirect:/auth/login?success=registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // Dang xuat
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // Xoa session
        return "redirect:/auth/login?success=logout";
    }
}