package com.cinema.controller;

import com.cinema.dto.MovieDTO;
import com.cinema.entity.Movie;
import com.cinema.repository.GenreRepository;
import com.cinema.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/movies")
public class AdminMovieController {

    @Autowired private MovieService movieService;
    @Autowired private GenreRepository genreRepository;

    // CORE-04: Danh sach phim (Admin)
    @GetMapping
    public String list(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "admin/movies/list";
    }

    // Form tao phim moi
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("dto", new MovieDTO());
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/movies/form";
    }

    // Form sua phim
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay phim"));
        // Chuyen Movie -> MovieDTO
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDuration(movie.getDuration());
        dto.setGenreId(movie.getGenre() != null ? movie.getGenre().getId() : null);
        dto.setReleaseDate(movie.getReleaseDate());

        model.addAttribute("dto", dto);
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("movie", movie);  // De hien anh cu
        return "admin/movies/form";
    }

    // Luu phim (tao moi hoac cap nhat)
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("dto") MovieDTO dto,
                       BindingResult result,
                       Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("genres", genreRepository.findAll());
            return "admin/movies/form";
        }
        try {
            if (dto.getId() == null) {
                movieService.createMovie(dto);
            } else {
                movieService.updateMovie(dto.getId(), dto);
            }
            return "redirect:/admin/movies?success=saved";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genres", genreRepository.findAll());
            return "admin/movies/form";
        }
    }

    // CORE-04: An phim (khong xoa that su)
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        movieService.deactivateMovie(id);
        return "redirect:/admin/movies?success=deleted";
    }
}