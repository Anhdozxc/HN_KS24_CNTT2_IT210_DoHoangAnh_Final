package com.cinema.controller;

import com.cinema.entity.Movie;
import com.cinema.entity.Showtime;
import com.cinema.service.BookingService;
import com.cinema.service.MovieService;
import com.cinema.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired private MovieService movieService;
    @Autowired private ShowtimeService showtimeService;
    @Autowired private BookingService bookingService;

    // Trang chu: hien danh sach phim dang chieu
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("movies", movieService.getActiveMovies());
        return "customer/home";
    }

    // Chi tiet phim + danh sach suat chieu
    @GetMapping("/movie/{id}")
    public String movieDetail(@PathVariable Long id, Model model) {
        Movie movie = movieService.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay phim"));
        List<Showtime> showtimes = showtimeService.getShowtimesByMovie(id);

        // Kiem tra tung suat chieu co het ve khong (CORE-08)
        Map<Long, Boolean> soldOutMap = new HashMap<>();
        for (Showtime s : showtimes) {
            soldOutMap.put(s.getId(), bookingService.isSoldOut(s));
        }

        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimes);
        model.addAttribute("soldOutMap", soldOutMap);
        return "customer/movie-detail";
    }
}