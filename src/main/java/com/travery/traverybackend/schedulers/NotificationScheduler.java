package com.travery.traverybackend.schedulers;

import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.enums.common.NotificationType;
import com.travery.traverybackend.enums.tour.TourInstanceStatus;
import com.travery.traverybackend.repositories.booking.HotelBookingRepository;
import com.travery.traverybackend.repositories.booking.TourBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.tour.TourInstanceRepository;
import com.travery.traverybackend.services.common.NotificationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

  private final TourInstanceRepository tourInstanceRepository;
  private final TourBookingRepository tourBookingRepository;
  private final HotelBookingRepository hotelBookingRepository;
  private final CoachBookingRepository coachBookingRepository;
  private final NotificationService notificationService;

  /** Runs daily at 08:00 AM to send reminders for bookings starting tomorrow. */
  @Scheduled(cron = "0 0 8 * * ?")
  public void sendUpcomingReminders() {
    sendUpcomingTourReminders();
    sendUpcomingHotelReminders();
    sendUpcomingCoachReminders();
  }

  private void sendUpcomingTourReminders() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    log.info("Starting upcoming tour reminders for date: {}", tomorrow);

    List<TourInstanceStatus> activeStatuses =
        List.of(TourInstanceStatus.OPEN, TourInstanceStatus.FULL);

    tourInstanceRepository
        .findByStartDateAndStatusIn(tomorrow, activeStatuses)
        .forEach(
            instance -> {
              tourBookingRepository
                  .findByTourInstanceIdAndStatus(instance.getId(), BookingStatus.PAID)
                  .forEach(
                      booking -> {
                        notificationService.sendToUser(
                            booking.getUser().getEmail(),
                            NotificationType.UPCOMING_TOUR,
                            "Chuẩn bị khởi hành!",
                            String.format(
                                "Chuyến đi %s của bạn sẽ khởi hành vào ngày mai. Hãy kiểm tra lại hành lý nhé!",
                                instance.getTour().getName()),
                            booking.getId().toString());
                      });
            });
  }

  private void sendUpcomingHotelReminders() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    log.info("Starting upcoming hotel reminders for date: {}", tomorrow);

    hotelBookingRepository
        .findByStartDateAndStatus(tomorrow, BookingStatus.PAID)
        .forEach(
            booking -> {
              notificationService.sendToUser(
                  booking.getUser().getEmail(),
                  NotificationType.UPCOMING_HOTEL,
                  "Nhắc nhở nhận phòng",
                  "Bạn có lịch nhận phòng khách sạn vào ngày mai. Đừng quên mang theo CCCD nhé!",
                  booking.getId().toString());
            });
  }

  private void sendUpcomingCoachReminders() {
    LocalDateTime tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay();
    LocalDateTime tomorrowEnd = tomorrowStart.plusDays(1).minusNanos(1);
    log.info("Starting upcoming coach reminders for date: {}", tomorrowStart.toLocalDate());

    coachBookingRepository
        .findByDepartureTimeBetweenAndStatus(tomorrowStart, tomorrowEnd, BookingStatus.PAID)
        .forEach(
            booking -> {
              notificationService.sendToUser(
                  booking.getUser().getEmail(),
                  NotificationType.UPCOMING_COACH,
                  "Nhắc nhở chuyến xe",
                  String.format(
                      "Chuyến xe từ %s đến %s của bạn sẽ khởi hành vào ngày mai (%s). Hãy có mặt tại bến sớm 15 phút nhé!",
                      booking.getCoachTrip().getRoute().getOriginDestination().getName(),
                      booking.getCoachTrip().getRoute().getDestinationDestination().getName(),
                      booking
                          .getCoachTrip()
                          .getDepartureTime()
                          .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))),
                  booking.getId().toString());
            });
  }
}
