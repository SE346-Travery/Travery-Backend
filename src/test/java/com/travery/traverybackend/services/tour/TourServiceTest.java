package com.travery.traverybackend.services.tour;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travery.traverybackend.dtos.request.tour.TourItineraryRequest;
import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.entities.finance.RefundPolicy;
import com.travery.traverybackend.entities.hotel.Hotel;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.user.*;
import com.travery.traverybackend.enums.finance.RefundServiceType;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.mappers.TourMapper;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.repositories.finance.RefundPolicyRepository;
import com.travery.traverybackend.repositories.hotel.HotelRepository;
import com.travery.traverybackend.repositories.tour.TourRepository;
import com.travery.traverybackend.repositories.user.UserRepository;
import com.travery.traverybackend.services.tour.impl.TourServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TourServiceTest {

  @Mock private TourRepository tourRepository;
  @Mock private DestinationRepository destinationRepository;
  @Mock private HotelRepository hotelRepository;
  @Mock private RefundPolicyRepository refundPolicyRepository;
  @Mock private UserRepository userRepository;
  @Mock private TourMapper tourMapper;

  @InjectMocks private TourServiceImpl tourService;

  private UUID coordinatorId;
  private Coordinator coordinator;
  private TourTemplateRequest request;
  private Destination destination;
  private Hotel hotel;

  @BeforeEach
  void setUp() {
    coordinatorId = UUID.randomUUID();
    coordinator = new Coordinator();
    coordinator.setId(coordinatorId);

    destination = new Destination();
    destination.setName("Da Lat");

    hotel = new Hotel();
    hotel.setName("Hotel A");

    request =
        TourTemplateRequest.builder()
            .name("Dalat Trip")
            .destinationId(UUID.randomUUID())
            .hotelId(UUID.randomUUID())
            .pickupLocation("Station 1")
            .pricePerAdult(new BigDecimal("1000"))
            .pricePerChild(new BigDecimal("500"))
            .itineraries(
                List.of(
                    TourItineraryRequest.builder()
                        .dayNumber(1)
                        .title("Day 1")
                        .description("Arrive")
                        .build()))
            .build();

    // Default mock for standard refund policy since it's used in most tests
    RefundPolicy standardPolicy = new RefundPolicy();
    standardPolicy.setName("Standard Tour Policy");
    lenient()
        .when(
            refundPolicyRepository.findByNameAndServiceType(
                "Standard Tour Policy", RefundServiceType.TOUR))
        .thenReturn(Optional.of(standardPolicy));
  }

  @Test
  void createTemplate_validRequest_returnsResponse() {
    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(destinationRepository.findById(request.getDestinationId()))
        .thenReturn(Optional.of(destination));
    when(hotelRepository.findById(request.getHotelId())).thenReturn(Optional.of(hotel));

    Tour tour = new Tour();
    when(tourMapper.toEntity(request)).thenReturn(tour);
    when(tourRepository.save(any(Tour.class))).thenReturn(tour);

    TourResponse response = new TourResponse();
    response.setName("Dalat Trip");
    when(tourMapper.toTourResponse(tour)).thenReturn(response);

    TourResponse result = tourService.createTemplate(request, null, null, coordinatorId);

    assertNotNull(result);
    assertEquals("Dalat Trip", result.getName());
    verify(tourRepository).save(any(Tour.class));
  }

  @Test
  void createTemplate_customTourWithUser_returnsResponse() {
    UUID requestedByUserId = UUID.randomUUID();
    request.setRequestedByUserId(requestedByUserId);
    request.setIsCustom(true);

    Tourist requestedUser = new Tourist();
    requestedUser.setId(requestedByUserId);

    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(destinationRepository.findById(request.getDestinationId()))
        .thenReturn(Optional.of(destination));
    when(hotelRepository.findById(request.getHotelId())).thenReturn(Optional.of(hotel));
    when(userRepository.findById(requestedByUserId)).thenReturn(Optional.of(requestedUser));

    Tour tour = new Tour();
    when(tourMapper.toEntity(request)).thenReturn(tour);
    when(tourRepository.save(any(Tour.class))).thenReturn(tour);

    TourResponse response = new TourResponse();
    response.setCustom(true);
    when(tourMapper.toTourResponse(tour)).thenReturn(response);

    TourResponse result = tourService.createTemplate(request, null, null, coordinatorId);

    assertNotNull(result);
    assertEquals(true, result.isCustom());
    verify(userRepository).findById(requestedByUserId);
  }

  @Test
  void createTemplate_nonTouristRequester_throwsException() {
    UUID requestedByUserId = UUID.randomUUID();
    request.setRequestedByUserId(requestedByUserId);
    request.setIsCustom(true);

    User nonTouristUser = new User();
    nonTouristUser.setId(requestedByUserId);

    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(destinationRepository.findById(request.getDestinationId()))
        .thenReturn(Optional.of(destination));
    when(hotelRepository.findById(request.getHotelId())).thenReturn(Optional.of(hotel));
    when(userRepository.findById(requestedByUserId)).thenReturn(Optional.of(nonTouristUser));

    assertThrows(
        BaseAppException.class,
        () -> tourService.createTemplate(request, null, null, coordinatorId));
  }

  @Test
  void createTemplate_noRefundPolicy_assignsStandard() {
    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(destinationRepository.findById(request.getDestinationId()))
        .thenReturn(Optional.of(destination));
    when(hotelRepository.findById(request.getHotelId())).thenReturn(Optional.of(hotel));

    Tour tour = new Tour();
    when(tourMapper.toEntity(request)).thenReturn(tour);
    when(tourRepository.save(any(Tour.class))).thenReturn(tour);

    TourResponse response = new TourResponse();
    when(tourMapper.toTourResponse(tour)).thenReturn(response);

    TourResponse result = tourService.createTemplate(request, null, null, coordinatorId);

    assertNotNull(result);
    verify(refundPolicyRepository)
        .findByNameAndServiceType("Standard Tour Policy", RefundServiceType.TOUR);
  }

  @Test
  void createTemplate_invalidCoordinator_throwsException() {
    when(userRepository.findById(coordinatorId)).thenReturn(Optional.empty());

    assertThrows(
        BaseAppException.class,
        () -> tourService.createTemplate(request, null, null, coordinatorId));
  }

  @Test
  void createTemplate_invalidDestination_throwsException() {
    when(userRepository.findById(coordinatorId)).thenReturn(Optional.of(coordinator));
    when(destinationRepository.findById(request.getDestinationId())).thenReturn(Optional.empty());

    assertThrows(
        BaseAppException.class,
        () -> tourService.createTemplate(request, null, null, coordinatorId));
  }
}
