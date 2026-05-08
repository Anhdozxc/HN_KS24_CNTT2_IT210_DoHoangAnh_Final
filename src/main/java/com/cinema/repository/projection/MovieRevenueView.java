package com.cinema.repository.projection;

import java.math.BigDecimal;

public interface MovieRevenueView {
    String getMovieTitle();
    BigDecimal getRevenue();
    Long getConfirmedBookings();
}
