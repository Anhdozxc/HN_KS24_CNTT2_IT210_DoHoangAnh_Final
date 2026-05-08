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
        Movie movie = getMovie(dto.getMovieId());
        Room room = getRoom(dto.getRoomId());
        LocalDateTime start = dto.getStartTime();
        LocalDateTime end = calculateEndTime(start, movie);
        validateConflict(room.getId(), start, end, null);

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

    @Transactional
    public void updateShowtime(Long id, ShowtimeDTO dto) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suat chieu khong ton tai"));

        Movie movie = getMovie(dto.getMovieId());
        Room room = getRoom(dto.getRoomId());
        LocalDateTime start = dto.getStartTime();
        LocalDateTime end = calculateEndTime(start, movie);

        validateConflict(room.getId(), start, end, id);

        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(start);
        showtime.setEndTime(end);
        showtime.setPrice(dto.getPrice());
        showtimeRepository.save(showtime);
    }

    private Movie getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Phim khong ton tai"));
    }

    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phong khong ton tai"));
    }

    private LocalDateTime calculateEndTime(LocalDateTime start, Movie movie) {
        return start.plusMinutes(movie.getDuration() + 15);
    }

    private void validateConflict(Long roomId, LocalDateTime start, LocalDateTime end, Long excludeId) {
        boolean conflict = excludeId == null
                ? showtimeRepository.existsConflict(roomId, start, end)
                : showtimeRepository.existsConflictExcluding(roomId, start, end, excludeId);

        if (conflict) {
            throw new RuntimeException(
                    "Phong chieu bi trung gio. Gio ket thuc du kien: " + end +
                            " (bao gom 15 phut don phong)"
            );
        }
    }
}
