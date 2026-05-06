package com.cinema.controller;

import com.cinema.dto.ProfileDTO;
import com.cinema.entity.User;
import com.cinema.entity.UserProfile;
import com.cinema.repository.UserProfileRepository;
import com.cinema.service.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired private UserProfileRepository profileRepository;
    @Autowired private CloudinaryService cloudinaryService;

    // Hien thi form sua ho so
    @GetMapping("/edit")
    public String editForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        UserProfile profile = profileRepository.findByUserId(user.getId()).orElse(new UserProfile());

        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(profile.getFullName());
        dto.setPhone(profile.getPhone());
        dto.setAddress(profile.getAddress());

        model.addAttribute("dto", dto);
        model.addAttribute("profile", profile);
        return "profile/edit";
    }

    // Luu ho so (CORE-03)
    @PostMapping("/save")
    public String save(@ModelAttribute ProfileDTO dto,
                       HttpSession session,
                       Model model) throws Exception {
        User user = (User) session.getAttribute("loggedUser");
        UserProfile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });

        profile.setFullName(dto.getFullName());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());

        // Upload anh dai dien moi neu co
        if (dto.getAvatarFile() != null && !dto.getAvatarFile().isEmpty()) {
            String url = cloudinaryService.upload(dto.getAvatarFile());
            profile.setAvatarUrl(url);
        }

        profileRepository.save(profile);
        return "redirect:/profile/edit?success=saved";
    }
}