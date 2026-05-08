package com.cinema.service;

import com.cinema.dto.RegisterDTO;
import com.cinema.entity.User;
import com.cinema.entity.UserProfile;
import com.cinema.repository.UserProfileRepository;
import com.cinema.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository profileRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // CORE-01: Đăng ký tài khoản
    @Transactional
    public void register(RegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));  // HASH mật khẩu
        userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName(dto.getFullName());
        profileRepository.save(profile);
    }

    // CORE-01: Đăng nhập - trả về User nếu đúng, null nếu sai
    public Optional<User> login(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(u -> encoder.matches(rawPassword, u.getPassword()));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}