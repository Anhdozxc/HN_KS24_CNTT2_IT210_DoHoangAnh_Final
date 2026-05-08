package com.cinema.service;

import com.cinema.dto.BookingRequestDTO;
import com.cinema.entity.*;
import com.cinema.entity.enums.BookingStatus;
import com.cinema.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private UserRepository userRepository;

    // CORE-07: Lich su dat ve cua khach hang
    public List<Booking> getBookingHistory(Long userId) {
        return bookingRepository.findByUserIdWithDetails(userId);
    }

    // Lay ve chi tiet cua 1 hoa don
    public List<Ticket> getTicketsByBooking(Long bookingId) {
        return ticketRepository.findByBookingIdWithSeat(bookingId);
    }

    // Lay danh sach seat ID da dat cho 1 suat chieu (de hien so do ghe)
    public List<Long> getBookedSeatIds(Long showtimeId) {
        return ticketRepository.findBookedSeatIdsByShowtimeId(showtimeId);
    }

    // Kiem tra suat chieu co het ve khong (de hien nhan "Het ve")
    public boolean isSoldOut(Showtime showtime) {
        int booked = ticketRepository.countByShowtimeId(showtime.getId());
        return booked >= showtime.getRoom().getTotalSeats();
    }

    // CORE-06: Dat ve (Co @Transactional: neu loi thi rollback toan bo)
    @Transactional
    public Booking createBooking(Long userId, BookingRequestDTO dto) {
        Showtime showtime = showtimeRepository.findById(dto.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Suat chieu khong ton tai"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nguoi dung khong ton tai"));

        if (dto.getSeatIds() == null || dto.getSeatIds().isEmpty()) {
            throw new RuntimeException("Vui long chon it nhat 1 ghe");
        }

        List<Long> requestedSeatIds = new ArrayList<>(new LinkedHashSet<>(dto.getSeatIds()));
        if (requestedSeatIds.size() != dto.getSeatIds().size()) {
            throw new RuntimeException("Danh sach ghe chua ID trung lap");
        }

        List<Seat> seats = seatRepository.findByRoomIdAndIdIn(showtime.getRoom().getId(), requestedSeatIds);
        if (seats.size() != requestedSeatIds.size()) {
            List<Long> validSeatIds = seats.stream().map(Seat::getId).toList();
            List<Long> invalidSeatIds = requestedSeatIds.stream()
                    .filter(seatId -> !validSeatIds.contains(seatId))
                    .toList();
            throw new RuntimeException("Ghe khong hop le cho suat chieu nay: " + invalidSeatIds);
        }

        Map<Long, Seat> seatMap = seats.stream()
                .collect(Collectors.toMap(Seat::getId, seat -> seat));

        // CORE-06: Kiem tra ghe co bi nguoi khac dat truoc khong
        int conflict = ticketRepository.countByShowtimeIdAndSeatIdIn(
                dto.getShowtimeId(), requestedSeatIds
        );
        if (conflict > 0) {
            // Rollback: nem exception de Spring tu dong rollback
            throw new RuntimeException(
                    "Ghe vua duoc nguoi khac dat, vui long chon lai."
            );
        }

        // Tinh tong tien
        BigDecimal totalPrice = showtime.getPrice()
                .multiply(BigDecimal.valueOf(requestedSeatIds.size()));

        // Tao hoa don
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Tao ve cho tung ghe duoc chon
        List<Ticket> tickets = new ArrayList<>();
        for (Long seatId : requestedSeatIds) {
            Seat seat = seatMap.get(seatId);
            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setSeat(seat);
            ticket.setShowtime(showtime);
            ticket.setPrice(showtime.getPrice());
            tickets.add(ticket);
        }

        try {
            ticketRepository.saveAllAndFlush(tickets);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Ghe vua duoc nguoi khac dat, vui long chon lai.");
        }

        return booking;
    }

    // CORE-09: Huy ve (truoc 24 gio)
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Hoa don khong ton tai"));

        // Kiem tra quyen so huu
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Ban khong co quyen huy ve nay");
        }

        // Kiem tra da huy chua
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Ve nay da bi huy truoc do");
        }

        // CORE-09: Chi cho huy truoc 24 gio
        if (!canCancel(booking)) {
            throw new RuntimeException("Chi duoc huy ve truoc 24 gio so voi gio chieu");
        }

        // Giai phong ghe: xoa tat ca tickets cua booking nay
        ticketRepository.deleteByBookingId(bookingId);

        // Cap nhat trang thai hoa don
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public boolean canCancel(Booking booking) {
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return false;
        }
        return LocalDateTime.now().isBefore(booking.getShowtime().getStartTime().minusHours(24));
    }
}
