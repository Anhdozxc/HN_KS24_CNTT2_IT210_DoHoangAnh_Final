package com.cinema.repository;

import com.cinema.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Lay ghe da dat cho 1 suat chieu (de hien so do ghe)
    @Query("SELECT t.seat.id FROM Ticket t " +
            "JOIN t.booking b " +
            "WHERE t.showtime.id = :showtimeId AND b.status != 'CANCELLED'")
    List<Long> findBookedSeatIdsByShowtimeId(@Param("showtimeId") Long showtimeId);

    // CORE-06: Kiem tra ghe co bi dat chua
    @Query("SELECT COUNT(t) FROM Ticket t " +
            "JOIN t.booking b " +
            "WHERE t.showtime.id = :showtimeId " +
            "AND t.seat.id IN :seatIds " +
            "AND b.status != 'CANCELLED'")
    int countByShowtimeIdAndSeatIdIn(@Param("showtimeId") Long showtimeId,
                                     @Param("seatIds") List<Long> seatIds);

    // Lay ve theo hoa don (hien thi chi tiet ghe)
    @Query("SELECT t FROM Ticket t JOIN FETCH t.seat WHERE t.booking.id = :bookingId ORDER BY t.seat.seatName")
    List<Ticket> findByBookingIdWithSeat(@Param("bookingId") Long bookingId);

    // CORE-09: Xoa ve khi huy (giai phong ghe)
    @Modifying
    @Query("DELETE FROM Ticket t WHERE t.booking.id = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);

    // Dem so ghe da dat cua 1 suat chieu (kiem tra het ve)
    @Query("SELECT COUNT(t) FROM Ticket t JOIN t.booking b " +
            "WHERE t.showtime.id = :showtimeId AND b.status != 'CANCELLED'")
    int countByShowtimeId(@Param("showtimeId") Long showtimeId);
}
