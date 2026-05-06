package com.cinema.repository;

import com.cinema.entity.Booking;
import com.cinema.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // CORE-07: Lich su dat ve cua khach hang (JOIN nhieu bang)
    @Query("SELECT DISTINCT b FROM Booking b " +
            "JOIN FETCH b.showtime s " +
            "JOIN FETCH s.movie m " +
            "JOIN FETCH s.room r " +
            "WHERE b.user.id = :userId " +
            "ORDER BY b.bookedAt DESC")
    List<Booking> findByUserIdWithDetails(@Param("userId") Long userId);

    // Cron Job: tim PENDING booking qua 15 phut
    List<Booking> findByStatusAndBookedAtBefore(BookingStatus status, LocalDateTime cutoff);

    // Staff: tim booking theo id de xac nhan
    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.user u " +
            "JOIN FETCH b.showtime s " +
            "JOIN FETCH s.movie " +
            "JOIN FETCH s.room " +
            "WHERE b.id = :id")
    java.util.Optional<Booking> findByIdWithDetails(@Param("id") Long id);
}