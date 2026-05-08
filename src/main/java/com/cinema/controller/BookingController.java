package com.cinema.controller;

import com.cinema.dto.BookingRequestDTO;
import com.cinema.entity.Booking;
import com.cinema.entity.Showtime;
import com.cinema.entity.Ticket;
import com.cinema.entity.User;
import com.cinema.service.BookingService;
import com.cinema.service.ShowtimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired private BookingService bookingService;
    @Autowired private ShowtimeService showtimeService;

    // Hien thi so do ghe cho 1 suat chieu
    @GetMapping("/seats/{showtimeId}")
    public String seatSelection(@PathVariable Long showtimeId, Model model) {
        Showtime showtime = showtimeService.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Suat chieu khong ton tai"));

        // Lay danh sach ghe da dat (de to mau khac)
        List<Long> bookedSeatIds = bookingService.getBookedSeatIds(showtimeId);

        model.addAttribute("showtime", showtime);
        // Lay tat ca ghe cua phong
        model.addAttribute("seats", showtime.getRoom().getSeats());
        model.addAttribute("bookedSeatIds", bookedSeatIds);
        return "customer/seats";
    }

    // Xu ly dat ve
    @PostMapping("/create")
    public String createBooking(@ModelAttribute BookingRequestDTO dto,
                                HttpSession session,
                                Model model) {
        User user = (User) session.getAttribute("loggedUser");
        try {
            Booking booking = bookingService.createBooking(user.getId(), dto);
            return "redirect:/booking/success/" + booking.getId();
        } catch (RuntimeException e) {
            String error = UriUtils.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/booking/seats/" + dto.getShowtimeId() + "?error=" + error;
        }
    }

    // Trang dat ve thanh cong
    @GetMapping("/success/{bookingId}")
    public String bookingSuccess(@PathVariable Long bookingId,
                                 HttpSession session,
                                 Model model) {
        User user = (User) session.getAttribute("loggedUser");
        // Lay tat ca booking cua user, loc theo id
        List<Booking> all = bookingService.getBookingHistory(user.getId());
        Booking booking = all.stream()
                .filter(b -> b.getId().equals(bookingId))
                .findFirst()
                .orElseThrow();
        List<Ticket> tickets = bookingService.getTicketsByBooking(bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("tickets", tickets);
        return "customer/booking-success";
    }

    // CORE-07: Lich su dat ve
    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        List<Booking> bookings = bookingService.getBookingHistory(user.getId());
        Set<Long> cancellableBookingIds = new HashSet<>();

        for (Booking booking : bookings) {
            boolean canCancel = booking.getStatus().name().equals("CONFIRMED")
                    && LocalDateTime.now().isBefore(booking.getShowtime().getStartTime().minusHours(24));
            if (canCancel) {
                cancellableBookingIds.add(booking.getId());
            }
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("cancellableBookingIds", cancellableBookingIds);
        return "customer/history";
    }

    // CORE-09: Huy ve
    @PostMapping("/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId,
                                HttpSession session,
                                Model model) {
        User user = (User) session.getAttribute("loggedUser");
        try {
            bookingService.cancelBooking(bookingId, user.getId());
            return "redirect:/booking/history?success=cancelled";
        } catch (RuntimeException e) {
            String error = UriUtils.encode(e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/booking/history?error=" + error;
        }
    }
}
