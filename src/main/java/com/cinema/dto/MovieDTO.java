package com.cinema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Getter @Setter
public class MovieDTO {

    private Long id;  // null khi tao moi, co gia tri khi sua

    @NotBlank(message = "Ten phim khong duoc trong")
    private String title;

    private String description;

    @Min(value = 1, message = "Thoi luong phai lon hon 0")
    private int duration;

    @NotNull(message = "Vui long chon the loai")
    private Long genreId;

    private LocalDate releaseDate;

    // File anh upload (co the null neu khong doi anh)
    private MultipartFile posterFile;
}