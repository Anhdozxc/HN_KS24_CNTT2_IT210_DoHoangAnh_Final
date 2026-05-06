package com.cinema.config;

import com.cinema.entity.*;
import com.cinema.entity.enums.Role;
import com.cinema.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired private GenreRepository genreRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository profileRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void run(ApplicationArguments args) {
        seedGenres();
        seedRooms();
        seedAdminUser();
    }

    // Seed the loai phim
    private void seedGenres() {
        if (genreRepository.count() > 0) return;
        List<Genre> genres = List.of(
                genre("Hanh Dong"), genre("Kinh Di"), genre("Tinh Cam"),
                genre("Hoat Hinh"), genre("Hai Huoc"), genre("Vien Tuong")
        );
        genreRepository.saveAll(genres);
    }

    // Tao 3 phong chieu va ghe ngoi
    private void seedRooms() {
        if (roomRepository.count() > 0) return;

        // Phong 1: 30 ghe (A-C, so 1-10)
        createRoom("Phong 1", List.of("A", "B", "C"), 10);
        // Phong 2: 40 ghe (A-D, so 1-10)
        createRoom("Phong 2", List.of("A", "B", "C", "D"), 10);
        // Phong 3: 50 ghe (A-E, so 1-10)
        createRoom("Phong 3", List.of("A", "B", "C", "D", "E"), 10);
    }

    // Tao admin mac dinh (username: admin, password: admin123)
    private void seedAdminUser() {
        if (userRepository.existsByUsername("admin")) return;

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@cinema.com");
        admin.setPassword(encoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        UserProfile profile = new UserProfile();
        profile.setUser(admin);
        profile.setFullName("Quan tri vien");
        profileRepository.save(profile);

        // Tao nhan vien mau
        User staff = new User();
        staff.setUsername("staff");
        staff.setEmail("staff@cinema.com");
        staff.setPassword(encoder.encode("staff123"));
        staff.setRole(Role.STAFF);
        userRepository.save(staff);

        UserProfile staffProfile = new UserProfile();
        staffProfile.setUser(staff);
        staffProfile.setFullName("Nhan vien rap");
        profileRepository.save(staffProfile);
    }

    // Helper: tao phong + ghe
    private void createRoom(String name, List<String> rows, int cols) {
        Room room = new Room();
        room.setName(name);
        room.setTotalSeats(rows.size() * cols);
        roomRepository.save(room);

        List<Seat> seats = new ArrayList<>();
        for (String row : rows) {
            for (int col = 1; col <= cols; col++) {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setSeatName(row + col);  // VD: A1, A2, B1...
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);
    }

    private Genre genre(String name) {
        Genre g = new Genre();
        g.setName(name);
        return g;
    }
}