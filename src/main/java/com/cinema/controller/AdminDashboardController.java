package com.cinema.controller;

import com.cinema.repository.projection.MovieRevenueView;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired private MovieRepository movieRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookingRepository bookingRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        BigDecimal totalRevenue = bookingRepository.getTotalConfirmedRevenue();
        List<MovieRevenueView> topMovies = bookingRepository.findTopMoviesByRevenue(PageRequest.of(0, 5));

        model.addAttribute("totalMovies", movieRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("topMovies", topMovies);
        return "admin/dashboard";
    }
}
