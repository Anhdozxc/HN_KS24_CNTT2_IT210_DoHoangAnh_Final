package com.cinema.job;

import com.cinema.entity.enums.BookingStatus;
import com.cinema.repository.BookingRepository;
import com.cinema.repository.TicketRepository;
import com.cinema.entity.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingCleanupJob {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private TicketRepository ticketRepository;

    // Cron Job: chay moi 1 phut (0 giay cua moi phut)
    // Format: "giay phut gio ngay thang thu"
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cancelExpiredPendingBookings() {
        // Tim cac booking PENDING da qua 15 phut
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);
        List<Booking> expired = bookingRepository.findByStatusAndBookedAtBefore(
                BookingStatus.PENDING, cutoff
        );

        if (expired.isEmpty()) return;

        System.out.println("[CronJob] Tim thay " + expired.size() + " booking het han, dang huy...");

        for (Booking booking : expired) {
            // Giai phong ghe: xoa tickets cua booking nay
            ticketRepository.deleteByBookingId(booking.getId());
            // Doi trang thai thanh CANCELLED
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }

        System.out.println("[CronJob] Da huy " + expired.size() + " booking.");
    }
}