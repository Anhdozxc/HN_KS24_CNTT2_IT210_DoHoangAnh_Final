package com.cinema.entity.enums;

public enum BookingStatus {
    PENDING,    // Cho thanh toan (Cron Job se huy sau 15 phut)
    CONFIRMED,  // Da xac nhan / thanh toan
    CANCELLED   // Da huy
}