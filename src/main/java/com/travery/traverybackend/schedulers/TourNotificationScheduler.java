package com.travery.traverybackend.schedulers;

import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.services.common.NotificationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TourNotificationScheduler {

  private final TourInstanceRepository tourInstanceRepository;
  private final TourBookingRepository tourBookingRepository;
  private final NotificationService notificationService;

  /**
   * Runs daily at 08:00 AM to send reminders for tours starting tomorrow.
   */
  @Scheduled(cron = "0 0 8 * * ?")
  public void sendUpcomingTourReminders() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    log.info("Starting upcoming tour reminders for date: {}", tomorrow);

    List<TourInstanceStatus> activeStatuses = List.of(TourInstanceStatus.OPEN, TourInstanceStatus.FULL);
    
    tourInstanceRepository.findByStartDateAndStatusIn(tomorrow, activeStatuses).forEach(instance -> {
      tourBookingRepository.findByTourInstanceIdAndStatus(instance.getId(), BookingStatus.PAID).forEach(booking -> {
        notificationService.sendToUser(
            booking.getUser().getEmail(),
            NotificationType.UPCOMING_TOUR,
            "Chuẩn bị khởi hành!",
            String.format("Chuyến đi %s của bạn sẽ khởi hành vào ngày mai. Hãy kiểm tra lại hành lý nhé!", 
                instance.getTour().getName()),
            booking.getId().toString()
        );
      });
    });
    
    log.info("Finished upcoming tour reminders for date: {}", tomorrow);
  }
}
