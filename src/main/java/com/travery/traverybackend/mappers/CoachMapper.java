package com.travery.traverybackend.mappers;

import com.travery.traverybackend.dtos.response.coach.CoachBookingDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachBookingSummaryResponse;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutItemResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.entities.booking.CoachBooking;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.SeatLayout;
import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import com.travery.traverybackend.entities.coach.CoachTrip;
import com.travery.traverybackend.entities.coach.Station;
import com.travery.traverybackend.entities.finance.PaymentTransaction;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import com.travery.traverybackend.enums.booking.BookingStatus;
import com.travery.traverybackend.repositories.coach.CoachBookingRepository;
import com.travery.traverybackend.repositories.coach.CoachBookingSeatRepository;

@Mapper(componentModel = "spring")
public abstract class CoachMapper {

    @Lazy
    @Autowired
    protected CoachBookingRepository coachBookingRepository;

    @Lazy
    @Autowired
    protected CoachBookingSeatRepository coachBookingSeatRepository;

        public abstract SeatLayoutItemResponse toSeatLayoutItemResponse(SeatLayoutItem seatLayoutItem);

        public abstract List<SeatLayoutItemResponse> toSeatLayoutItemResponseList(List<SeatLayoutItem> items);

        @Mapping(target = "items", source = "items")
        public abstract SeatLayoutResponse toSeatLayoutResponse(SeatLayout seatLayout);

        public abstract List<SeatLayoutResponse> toSeatLayoutResponseList(List<SeatLayout> seatLayouts);

        @Mapping(target = "seatLayoutName", source = "seatLayout.name")
        public abstract CoachResponse toCoachResponse(Coach coach);

        public abstract List<CoachResponse> toCoachResponseList(List<Coach> coaches);

        @Mapping(target = "coachType", source = "trip.coach.coachType")
        @Mapping(target = "totalSeats", source = "trip.coach.seatLayout.totalSeats")
        @Mapping(target = "basePrice", source = "trip.route.basePrice")
        @Mapping(target = "originDestination", source = "trip.route.originDestination")
        @Mapping(target = "destinationDestination", source = "trip.route.destinationDestination")
        @Mapping(target = "availableSeats", source = "availableSeats")
        public abstract CoachTripResponse toCoachTripResponse(CoachTrip trip, int availableSeats);

        @Mapping(target = "id", source = "trip.id")
        @Mapping(target = "routeId", source = "trip.route.id")
        @Mapping(target = "originDestinationName", source = "trip.route.originDestination.name")
        @Mapping(target = "destinationDestinationName", source = "trip.route.destinationDestination.name")
        @Mapping(target = "basePrice", source = "trip.route.basePrice")
        @Mapping(target = "coachId", source = "trip.coach.id")
        @Mapping(target = "coachLicensePlate", source = "trip.coach.licensePlate")
        @Mapping(target = "coachType", source = "trip.coach.coachType")
        @Mapping(target = "totalSeats", source = "trip.coach.seatLayout.totalSeats")
        @Mapping(target = "driverId", source = "trip.driver.id")
        @Mapping(target = "driverName", source = "trip.driver.fullName")
        @Mapping(target = "driverPhone", source = "trip.driver.phoneNumber")
        @Mapping(target = "bookingsCount", ignore = true)
        @Mapping(target = "passengersCount", ignore = true)
        @Mapping(target = "availableSeats", ignore = true)
        public abstract CoachTripDetailResponse toCoachTripDetailResponse(CoachTrip trip);

        @AfterMapping
        protected void fillDetailFields(CoachTrip trip, @MappingTarget CoachTripDetailResponse response) {
                int totalSeats = trip.getCoach() != null && trip.getCoach().getSeatLayout() != null 
                        ? trip.getCoach().getSeatLayout().getTotalSeats() : 0;
                int bookingsCount = coachBookingRepository.countByCoachTrip_Id(trip.getId());
                int passengersCount = coachBookingSeatRepository.countByCoachTripIdAndBookingStatus(
                        trip.getId(), BookingStatus.PAID);
                int unavailableSeats = coachBookingSeatRepository.findByTripIdAndBookingStatusNotIn(
                        trip.getId(), List.of(BookingStatus.CANCELLED, BookingStatus.NO_SHOW)).size();

                response.setBookingsCount(bookingsCount);
                response.setPassengersCount(passengersCount);
                response.setAvailableSeats(totalSeats - unavailableSeats);
        }

        @Mapping(source = "destination.id", target = "destinationId")
        @Mapping(source = "destination.name", target = "destinationName")
        public abstract StationResponse toStationResponse(Station station);

        public abstract List<StationResponse> toStationResponseList(List<Station> stations);

        public abstract com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse toDestinationWithStationsResponse(
            com.travery.traverybackend.entities.common.Destination destination);

        public abstract List<com.travery.traverybackend.dtos.response.coach.DestinationWithStationsResponse> toDestinationWithStationsResponseList(
            List<com.travery.traverybackend.entities.common.Destination> destinations);

        @Mapping(target = "departureTime", source = "booking.coachTrip.departureTime")
        @Mapping(target = "originDestination", source = "booking.coachTrip.route.originDestination.name")
        @Mapping(target = "destinationDestination", source = "booking.coachTrip.route.destinationDestination.name")
        public abstract CoachBookingSummaryResponse toCoachBookingSummaryResponse(
                        CoachBooking booking, int seatCount);

        @Mapping(target = "id", source = "booking.id")
        @Mapping(target = "status", source = "booking.status")
        @Mapping(target = "tripId", source = "booking.coachTrip.id")
        @Mapping(target = "departureTime", source = "booking.coachTrip.departureTime")
        @Mapping(target = "estimatedArrivalTime", source = "booking.coachTrip.arrivalTime")
        @Mapping(target = "originDestination", source = "booking.coachTrip.route.originDestination.name")
        @Mapping(target = "destinationDestination", source = "booking.coachTrip.route.destinationDestination.name")
        @Mapping(target = "coachLicensePlate", source = "booking.coachTrip.coach.licensePlate")
        @Mapping(target = "paymentMethod", source = "payment.paymentMethod")
        @Mapping(target = "paymentStatus", source = "payment.status")
        @Mapping(target = "transactionId", source = "payment.id")
        @Mapping(target = "gatewayTransactionId", source = "payment.transactionReference")
        public abstract CoachBookingDetailResponse toCoachBookingDetailResponse(
                        CoachBooking booking,
                        List<String> bookedSeatNames,
                        PaymentTransaction payment);
}
