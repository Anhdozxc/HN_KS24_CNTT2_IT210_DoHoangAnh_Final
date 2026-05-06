package com.cinema.service;

import com.cinema.dto.ShowtimeDTO;
import com.cinema.entity.Movie;
import com.cinema.entity.Room;
import com.cinema.entity.Showtime;
import com.cinema.repository.MovieRepository;
import com.cinema.repository.RoomRepository;
import com.cinema.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private RoomRepository roomRepository;

    // CORE-08: Lay suat chieu chua het gio
    public List<Showtime> getAvailableShowtimes() {
        return showtimeRepository.findAvailableShowtimes(LocalDateTime.now());
    }

    public List<Showtime> getShowtimesByMovie(Long movieId) {
        return showtimeRepository.findByMovieIdAndStartTimeAfter(movieId, LocalDateTime.now());
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    public Optional<Showtime> findById(Long id) {
        return showtimeRepository.findById(id);
    }

    // CORE-05: Tao suat chieu (co kiem tra xung dot phong)
    @Transactional
    public void createShowtime(ShowtimeDTO dto) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Phim khong ton tai"));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Phong khong ton tai"));

        LocalDateTime start = dto.getStartTime();
        // Gio ket thuc = gio bat dau + thoi luong phim + 15 phut don phong
        LocalDateTime end = start.plusMinutes(movie.getDuration() + 15);

        // CORE-05: Kiem tra phong co bi trung gio khong
        if (showtimeRepository.existsConflict(room.getId(), start, end)) {
            throw new RuntimeException(
                    "Phong chieu bi trung gio. Gio ket thuc du kien: " + end +
                            " (bao gom 15 phut don phong)"
            );
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(start);
        showtime.setEndTime(end);
        showtime.setPrice(dto.getPrice());
        showtimeRepository.save(showtime);
    }

    // Xoa suat chieu
    @Transactional
    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }
}