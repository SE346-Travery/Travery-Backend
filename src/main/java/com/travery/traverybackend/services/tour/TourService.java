package com.travery.traverybackend.services.tour;

import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourItinerary;
import com.travery.traverybackend.entities.user.*;
import com.travery.traverybackend.enums.finance.RefundServiceType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.mappers.TourMapper;
import com.travery.traverybackend.repositories.HotelRepository;
import com.travery.traverybackend.repositories.UserRepository;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.repositories.finance.RefundPolicyRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TourService {

  private final TourRepository tourRepository;
  private final DestinationRepository destinationRepository;
  private final HotelRepository hotelRepository;
  private final RefundPolicyRepository refundPolicyRepository;
  private final UserRepository userRepository;
  private final TourMapper tourMapper;

  @Transactional
  public TourResponse createTemplate(TourTemplateRequest request, UUID coordinatorId) {
    Coordinator coordinator =
        userRepository
            .findById(coordinatorId)
            .filter(user -> user instanceof Coordinator)
            .map(user -> (Coordinator) user)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.FORBIDDEN, "User is not a coordinator"));

    Destination destination =
        destinationRepository
            .findById(request.getDestinationId())
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Destination not found"));

    Hotel hotel = null;
    if (request.getHotelId() != null) {
      hotel =
          hotelRepository
              .findById(request.getHotelId())
              .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "Hotel not found"));
    }

    RefundPolicy refundPolicy =
        refundPolicyRepository
            .findByNameAndServiceType("Standard Tour Policy", RefundServiceType.TOUR)
            .orElseThrow(
                () -> new BaseAppException(WebErrorCode.NOT_FOUND, "Refund policy not found"));

    User requestedByUser = null;
    if (request.getRequestedByUserId() != null) {
      requestedByUser =
          userRepository
              .findById(request.getRequestedByUserId())
              .orElseThrow(() -> new BaseAppException(WebErrorCode.NOT_FOUND, "User not found"));

      if (!(requestedByUser instanceof Tourist)) {
        throw new BaseAppException(
            WebErrorCode.BAD_REQUEST, "Only Tourists can request custom tours");
      }
    }

    Tour tour = tourMapper.toEntity(request);
    tour.setCoordinator(coordinator);
    tour.setDestination(destination);
    tour.setHotel(hotel);
    tour.setRefundPolicy(refundPolicy);
    tour.setRequestedByUser(requestedByUser);
    tour.setCustom(Boolean.TRUE.equals(request.getIsCustom()));

    List<TourItinerary> itineraries =
        request.getItineraries().stream()
            .map(
                itineraryRequest ->
                    TourItinerary.builder()
                        .tour(tour)
                        .dayNumber(itineraryRequest.getDayNumber())
                        .title(itineraryRequest.getTitle())
                        .description(itineraryRequest.getDescription())
                        .build())
            .collect(Collectors.toList());

    tour.setItineraries(itineraries);

    Tour savedTour = tourRepository.save(tour);
    return tourMapper.toResponse(savedTour);
  }
}
