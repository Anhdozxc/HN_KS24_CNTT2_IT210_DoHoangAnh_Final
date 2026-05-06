package com.cinema.controller;

import com.cinema.dto.ShowtimeDTO;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.RoomRepository;
import com.cinema.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/showtimes")
public class AdminShowtimeController {

    @Autowired private ShowtimeService showtimeService;
    @Autowired private MovieRepository movieRepository;
    @Autowired private RoomRepository roomRepository;

    // Danh sach suat chieu
    @GetMapping
    public String list(Model model) {
        model.addAttribute("showtimes", showtimeService.getAllShowtimes());
        return "admin/showtimes/list";
    }

    // Form tao suat chieu
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("dto", new ShowtimeDTO());
        model.addAttribute("movies", movieRepository.findByActiveTrue());
        model.addAttribute("rooms", roomRepository.findAll());
        return "admin/showtimes/form";
    }

    // Tao suat chieu moi (CORE-05)
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("dto") ShowtimeDTO dto,
                       BindingResult result,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("movies", movieRepository.findByActiveTrue());
            model.addAttribute("rooms", roomRepository.findAll());
            return "admin/showtimes/form";
        }
        try {
            showtimeService.createShowtime(dto);
            return "redirect:/admin/showtimes?success=created";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("movies", movieRepository.findByActiveTrue());
            model.addAttribute("rooms", roomRepository.findAll());
            return "admin/showtimes/form";
        }
    }

    // Xoa suat chieu
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return "redirect:/admin/showtimes?success=deleted";
    }
}