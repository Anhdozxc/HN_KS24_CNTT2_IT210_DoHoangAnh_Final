package com.cinema.service;

import com.cinema.dto.MovieDTO;
import com.cinema.entity.Genre;
import com.cinema.entity.Movie;
import com.cinema.repository.GenreRepository;
import com.cinema.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired private MovieRepository movieRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private CloudinaryService cloudinaryService;

    // CORE-04: Lay danh sach phim dang chieu
    public List<Movie> getActiveMovies() {
        return movieRepository.findByActiveTrue();
    }

    // CORE-04: Lay tat ca phim (cho admin)
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    // CORE-04: Tao phim moi
    @Transactional
    public void createMovie(MovieDTO dto) throws IOException {
        Movie movie = new Movie();
        fillMovieFromDTO(movie, dto);
        movieRepository.save(movie);
    }

    // CORE-04: Sua phim
    @Transactional
    public void updateMovie(Long id, MovieDTO dto) throws IOException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay phim"));
        fillMovieFromDTO(movie, dto);
        movieRepository.save(movie);
    }

    // CORE-04: An phim (khong xoa han de giu du lieu lich su)
    @Transactional
    public void deactivateMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay phim"));
        movie.setActive(false);
        movieRepository.save(movie);
    }

    // Dien du lieu tu DTO vao Movie entity
    private void fillMovieFromDTO(Movie movie, MovieDTO dto) throws IOException {
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setDuration(dto.getDuration());
        movie.setReleaseDate(dto.getReleaseDate());

        Genre genre = genreRepository.findById(dto.getGenreId())
                .orElseThrow(() -> new RuntimeException("The loai khong ton tai"));
        movie.setGenre(genre);

        // Upload anh neu co chon anh moi
        if (dto.getPosterFile() != null && !dto.getPosterFile().isEmpty()) {
            String posterUrl = cloudinaryService.upload(dto.getPosterFile());
            movie.setPosterUrl(posterUrl);
        }
    }
}