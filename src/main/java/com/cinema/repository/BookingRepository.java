package com.cinema.repository;

import com.cinema.entity.Booking;
import com.cinema.entity.enums.BookingStatus;
import com.cinema.repository.projection.MovieRevenueView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // CORE-07: Lich su dat ve cua khach hang (JOIN nhieu bang)
    @Query("SELECT DISTINCT b FROM Booking b " +
            "JOIN FETCH b.showtime s " +
            "JOIN FETCH s.movie " +
            "JOIN FETCH s.room " +
            "LEFT JOIN FETCH b.tickets t " +
            "LEFT JOIN FETCH t.seat " +
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

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status = :status")
    BigDecimal getTotalRevenueByStatus(@Param("status") BookingStatus status);

    @Query("SELECT s.movie.title AS movieTitle, COALESCE(SUM(b.totalPrice), 0) AS revenue, COUNT(b) AS confirmedBookings " +
            "FROM Booking b " +
            "JOIN b.showtime s " +
            "WHERE b.status = :status " +
            "GROUP BY s.movie.id, s.movie.title " +
            "ORDER BY COALESCE(SUM(b.totalPrice), 0) DESC")
    List<MovieRevenueView> findTopMoviesByRevenue(@Param("status") BookingStatus status, Pageable pageable);
}
