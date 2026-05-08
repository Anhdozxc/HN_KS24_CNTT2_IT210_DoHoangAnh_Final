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

    // Seed the loại phim
    private void seedGenres() {
        if (genreRepository.count() > 0) return;
        List<Genre> genres = List.of(
                genre("Hành Động"), genre("Kinh Dị"), genre("Tình Cảm"),
                genre("Hoạt Hình"), genre("Hài Hước"), genre("Viễn Tưởng")
        );
        genreRepository.saveAll(genres);
    }

    // Tạo 3 phòng chiếu và ghế ngồi
    private void seedRooms() {
        if (roomRepository.count() > 0) return;

        // Phòng 1: 30 ghế (A-C, số 1-10)
        createRoom("Phòng 1", List.of("A", "B", "C"), 10);
        // Phòng 2: 40 ghế (A-D, số 1-10)
        createRoom("Phòng 2", List.of("A", "B", "C", "D"), 10);
        // Phòng 3: 50 ghế (A-E, số 1-10)
        createRoom("Phòng 3", List.of("A", "B", "C", "D", "E"), 10);
    }

    // Tạo admin mặc định (username: admin, password: admin123)
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
        profile.setFullName("Quản trị viên");
        profileRepository.save(profile);

        // Tạo nhân viên mẫu
        User staff = new User();
        staff.setUsername("staff");
        staff.setEmail("staff@cinema.com");
        staff.setPassword(encoder.encode("staff123"));
        staff.setRole(Role.STAFF);
        userRepository.save(staff);

        UserProfile staffProfile = new UserProfile();
        staffProfile.setUser(staff);
        staffProfile.setFullName("Nhân viên rạp");
        profileRepository.save(staffProfile);
    }

    // Helper: tạo phòng + ghế
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