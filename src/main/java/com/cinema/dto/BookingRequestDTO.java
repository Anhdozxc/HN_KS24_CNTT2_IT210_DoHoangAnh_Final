package com.cinema.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class BookingRequestDTO {

    @NotNull
    private Long showtimeId;

    @NotEmpty(message = "Vui long chon it nhat 1 ghe")
    private List<Long> seatIds;  // Danh sach ID ghe duoc chon
}