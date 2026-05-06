package com.cinema.repository;

import com.cinema.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    // Lay tat ca ghe cua 1 phong (de hien so do ghe)
    List<Seat> findByRoomId(Long roomId);
}