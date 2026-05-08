package com.cinema.controller;

import com.cinema.dto.ShowtimeDTO;
import com.cinema.entity.Showtime;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.RoomRepository;
import com.cinema.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/showtimes")
public class AdminShowtimeController {

    @Autowired private ShowtimeService showtimeService;
    @Autowired private MovieRepository movieRepository;
    @Autowired private RoomRepository roomRepository;

    // Danh sach suat chieu
    @GetMapping
    public String list(Model model) {
        var showtimes = showtimeService.getAllShowtimes();
        Map<Long, String> statusMap = new HashMap<>();

        for (Showtime showtime : showtimes) {
            statusMap.put(showtime.getId(), showtimeService.getShowtimeStatus(showtime));
        }

        model.addAttribute("showtimes", showtimes);
        model.addAttribute("statusMap", statusMap);
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
            if (dto.getId() == null) {
                showtimeService.createShowtime(dto);
                return "redirect:/admin/showtimes?success=created";
            }
            showtimeService.updateShowtime(dto.getId(), dto);
            return "redirect:/admin/showtimes?success=updated";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("movies", movieRepository.findByActiveTrue());
            model.addAttribute("rooms", roomRepository.findAll());
            return "admin/showtimes/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Showtime showtime = showtimeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Suat chieu khong ton tai"));

        ShowtimeDTO dto = new ShowtimeDTO();
        dto.setId(showtime.getId());
        dto.setMovieId(showtime.getMovie().getId());
        dto.setRoomId(showtime.getRoom().getId());
        dto.setStartTime(showtime.getStartTime());
        dto.setPrice(showtime.getPrice());

        model.addAttribute("dto", dto);
        model.addAttribute("movies", movieRepository.findByActiveTrue());
        model.addAttribute("rooms", roomRepository.findAll());
        return "admin/showtimes/form";
    }

    // Xoa suat chieu
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return "redirect:/admin/showtimes?success=deleted";
    }
}
