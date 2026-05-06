package com.cinema.repository;

import com.cinema.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // CORE-08: Lay suat chieu chua het gio (startTime > now)
    // JOIN FETCH de tranh N+1 query (load movie va room cung luc)
    @Query("SELECT s FROM Showtime s JOIN FETCH s.movie m JOIN FETCH s.room r " +
            "WHERE s.startTime > :now ORDER BY s.startTime")
    List<Showtime> findAvailableShowtimes(@Param("now") LocalDateTime now);

    // Lay suat chieu cua 1 phim (cho trang chi tiet phim)
    @Query("SELECT s FROM Showtime s JOIN FETCH s.room r " +
            "WHERE s.movie.id = :movieId AND s.startTime > :now ORDER BY s.startTime")
    List<Showtime> findByMovieIdAndStartTimeAfter(@Param("movieId") Long movieId,
                                                  @Param("now") LocalDateTime now);

    // CORE-05: Kiem tra xung dot phong chieu
    // Overlap: suatA.start < suatB.end AND suatA.end > suatB.start
    @Query("SELECT COUNT(s) > 0 FROM Showtime s " +
            "WHERE s.room.id = :roomId " +
            "AND s.startTime < :endTime AND s.endTime > :startTime")
    boolean existsConflict(@Param("roomId") Long roomId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);

    // (Cho sua suat chieu: loai tru chinh no ra khi check conflict)
    @Query("SELECT COUNT(s) > 0 FROM Showtime s " +
            "WHERE s.room.id = :roomId AND s.id != :excludeId " +
            "AND s.startTime < :endTime AND s.endTime > :startTime")
    boolean existsConflictExcluding(@Param("roomId") Long roomId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("excludeId") Long excludeId);
}