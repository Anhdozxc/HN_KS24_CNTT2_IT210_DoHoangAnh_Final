package com.cinema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "movies")
@Getter @Setter
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int duration;  // Thoi luong phim (phut)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    private String posterUrl;   // URL anh tu Cloudinary
    private LocalDate releaseDate;
    private boolean active = true;  // Dung de an/hien phim
}