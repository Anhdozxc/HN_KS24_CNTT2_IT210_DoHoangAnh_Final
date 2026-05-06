package com.cinema.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
public class ShowtimeDTO {

    @NotNull(message = "Vui long chon phim")
    private Long movieId;

    @NotNull(message = "Vui long chon phong")
    private Long roomId;

    @NotNull(message = "Vui long chon gio chieu")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")  // format HTML datetime-local
    private LocalDateTime startTime;

    @NotNull(message = "Vui long nhap gia ve")
    @Positive(message = "Gia ve phai lon hon 0")
    private BigDecimal price;
}