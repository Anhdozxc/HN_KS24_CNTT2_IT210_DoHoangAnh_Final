package com.cinema.controller;

import com.cinema.entity.Booking;
import com.cinema.entity.Ticket;
import com.cinema.repository.BookingRepository;
import com.cinema.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private BookingService bookingService;

    // Trang tra cuu don hang cua nhan vien
    @GetMapping("/orders")
    public String orders(Model model) {
        return "staff/orders";
    }

    // Tra cuu don theo booking ID
    @GetMapping("/orders/search")
    public String searchOrder(@RequestParam Long bookingId, Model model) {
        bookingRepository.findByIdWithDetails(bookingId).ifPresentOrElse(
                booking -> {
                    List<Ticket> tickets = bookingService.getTicketsByBooking(bookingId);
                    model.addAttribute("booking", booking);
                    model.addAttribute("tickets", tickets);
                },
                () -> model.addAttribute("error", "Khong tim thay don hang so " + bookingId)
        );
        return "staff/orders";
    }
}