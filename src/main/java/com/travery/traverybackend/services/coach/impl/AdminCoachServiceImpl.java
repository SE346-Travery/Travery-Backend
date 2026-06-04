package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.CreateStationRequest;
import com.travery.traverybackend.dtos.request.coach.SeatLayoutItemRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateStationRequest;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.DriverResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.Driver;
import com.travery.traverybackend.entities.coach.SeatLayout;
import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import com.travery.traverybackend.entities.coach.Station;
import com.travery.traverybackend.entities.common.Destination;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachRepository;
import com.travery.traverybackend.repositories.coach.DriverRepository;
import com.travery.traverybackend.repositories.coach.SeatLayoutRepository;
import com.travery.traverybackend.repositories.coach.StationRepository;
import com.travery.traverybackend.repositories.common.DestinationRepository;
import com.travery.traverybackend.services.coach.AdminCoachService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCoachServiceImpl implements AdminCoachService {

  private final SeatLayoutRepository seatLayoutRepository;
  private final CoachRepository coachRepository;
  private final DriverRepository driverRepository;
  private final StationRepository stationRepository;
  private final DestinationRepository destinationRepository;
  private final CoachMapper coachMapper;

  @Override
  @Transactional
  public SeatLayoutResponse createSeatLayout(CreateSeatLayoutRequest request) {
    SeatLayout layout =
        SeatLayout.builder()
            .name(request.getName())
            .coachType(request.getCoachType())
            .totalSeats(request.getItems().size())
            .build();

    List<SeatLayoutItem> items =
        request.getItems().stream().map(itemReq -> buildSeatLayoutItem(itemReq, layout)).toList();

    layout.setItems(items);
    SeatLayout saved = seatLayoutRepository.save(layout);
    log.info("Created seat layout '{}' with {} seats", saved.getName(), saved.getTotalSeats());

    return coachMapper.toSeatLayoutResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SeatLayoutResponse> getSeatLayouts(CoachType coachType) {
    List<SeatLayout> layouts =
        coachType != null
            ? seatLayoutRepository.findByCoachTypeWithItems(coachType)
            : seatLayoutRepository.findAllWithItems();

    return coachMapper.toSeatLayoutResponseList(layouts);
  }

  @Override
  @Transactional(readOnly = true)
  public SeatLayoutResponse getSeatLayoutDetail(UUID layoutId) {
    SeatLayout layout =
        seatLayoutRepository
            .findByIdWithItems(layoutId)
            .orElseThrow(
                () -> new EntityNotFoundException("Seat layout not found with id: " + layoutId));

    return coachMapper.toSeatLayoutResponse(layout);
  }

  @Override
  @Transactional
  public CoachResponse createCoach(CreateCoachRequest request) {
    SeatLayout layout = getSeatLayoutById(request.getSeatLayoutId());

    Coach coach =
        Coach.builder()
            .licensePlate(request.getLicensePlate())
            .coachType(request.getCoachType())
            .capacity(request.getCapacity())
            .seatLayout(layout)
            .build();

    Coach saved = coachRepository.save(coach);
    log.info(
        "Created coach '{}' (type={}) with layout '{}'",
        saved.getLicensePlate(),
        saved.getCoachType(),
        layout.getName());

    return coachMapper.toCoachResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CoachResponse> getCoaches() {
    return coachMapper.toCoachResponseList(coachRepository.findAllByIsDeletedFalse());
  }

  @Override
  @Transactional(readOnly = true)
  public CoachResponse getCoachDetail(UUID coachId) {
    Coach coach = getActiveCoachById(coachId);
    return coachMapper.toCoachResponse(coach);
  }

  @Override
  @Transactional
  public CoachResponse updateCoach(UUID coachId, UpdateCoachRequest request) {
    Coach coach = getActiveCoachById(coachId);

    if (request.getLicensePlate() != null && !request.getLicensePlate().isBlank()) {
      coach.setLicensePlate(request.getLicensePlate());
    }
    if (request.getCoachType() != null) {
      coach.setCoachType(request.getCoachType());
    }
    if (request.getCapacity() != null) {
      coach.setCapacity(request.getCapacity());
    }
    if (request.getStatus() != null) {
      coach.setStatus(request.getStatus());
    }
    if (request.getSeatLayoutId() != null) {
      coach.setSeatLayout(getSeatLayoutById(request.getSeatLayoutId()));
    }

    Coach saved = coachRepository.save(coach);
    return coachMapper.toCoachResponse(saved);
  }

  @Override
  @Transactional
  public void deleteCoach(UUID coachId) {
    Coach coach = getActiveCoachById(coachId);
    coach.setDeleted(true);
    coachRepository.save(coach);
  }

  @Override
  @Transactional
  public DriverResponse createDriver(CreateDriverRequest request) {
    Driver driver =
        Driver.builder()
            .fullName(request.getFullName())
            .phoneNumber(request.getPhoneNumber())
            .licenseNumber(request.getLicenseNumber())
            .avatarUrl(request.getAvatarUrl())
            .avatarPublicId(request.getAvatarPublicId())
            .build();

    Driver saved = driverRepository.save(driver);
    return coachMapper.toDriverResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DriverResponse> getDrivers() {
    return coachMapper.toDriverResponseList(driverRepository.findAllByIsDeletedFalse());
  }

  @Override
  @Transactional(readOnly = true)
  public DriverResponse getDriverDetail(UUID driverId) {
    return coachMapper.toDriverResponse(getActiveDriverById(driverId));
  }

  @Override
  @Transactional
  public DriverResponse updateDriver(UUID driverId, UpdateDriverRequest request) {
    Driver driver = getActiveDriverById(driverId);

    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      driver.setFullName(request.getFullName());
    }
    if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
      driver.setPhoneNumber(request.getPhoneNumber());
    }
    if (request.getLicenseNumber() != null && !request.getLicenseNumber().isBlank()) {
      driver.setLicenseNumber(request.getLicenseNumber());
    }
    if (request.getAvatarUrl() != null) {
      driver.setAvatarUrl(request.getAvatarUrl());
    }
    if (request.getAvatarPublicId() != null) {
      driver.setAvatarPublicId(request.getAvatarPublicId());
    }
    if (request.getStatus() != null) {
      driver.setStatus(request.getStatus());
    }

    Driver saved = driverRepository.save(driver);
    return coachMapper.toDriverResponse(saved);
  }

  @Override
  @Transactional
  public void deleteDriver(UUID driverId) {
    Driver driver = getActiveDriverById(driverId);
    driver.setDeleted(true);
    driverRepository.save(driver);
  }

  @Override
  @Transactional
  public StationResponse createStation(CreateStationRequest request) {
    Destination destination = getDestinationById(request.getDestinationId());

    Station station =
        Station.builder()
            .name(request.getName())
            .address(request.getAddress())
            .destination(destination)
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .build();

    Station saved = stationRepository.save(station);
    return coachMapper.toStationResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<StationResponse> getStations() {
    return coachMapper.toStationResponseList(stationRepository.findAllByIsDeletedFalse());
  }

  @Override
  @Transactional(readOnly = true)
  public StationResponse getStationDetail(UUID stationId) {
    return coachMapper.toStationResponse(getActiveStationById(stationId));
  }

  @Override
  @Transactional
  public StationResponse updateStation(UUID stationId, UpdateStationRequest request) {
    Station station = getActiveStationById(stationId);

    if (request.getName() != null && !request.getName().isBlank()) {
      station.setName(request.getName());
    }
    if (request.getAddress() != null && !request.getAddress().isBlank()) {
      station.setAddress(request.getAddress());
    }
    if (request.getDestinationId() != null) {
      station.setDestination(getDestinationById(request.getDestinationId()));
    }
    if (request.getLatitude() != null) {
      station.setLatitude(request.getLatitude());
    }
    if (request.getLongitude() != null) {
      station.setLongitude(request.getLongitude());
    }

    Station saved = stationRepository.save(station);
    return coachMapper.toStationResponse(saved);
  }

  @Override
  @Transactional
  public void deleteStation(UUID stationId) {
    Station station = getActiveStationById(stationId);
    station.setDeleted(true);
    stationRepository.save(station);
  }

  private SeatLayoutItem buildSeatLayoutItem(SeatLayoutItemRequest req, SeatLayout layout) {
    return SeatLayoutItem.builder()
        .seatLayout(layout)
        .seatName(req.getSeatName())
        .tier(req.getTier())
        .position(req.getPosition())
        .rowNumber(req.getRowNumber())
        .columnNumber(req.getColumnNumber())
        .build();
  }

  private SeatLayout getSeatLayoutById(UUID layoutId) {
    return seatLayoutRepository
        .findById(layoutId)
        .orElseThrow(
            () -> new EntityNotFoundException("Seat layout not found with id: " + layoutId));
  }

  private Coach getActiveCoachById(UUID coachId) {
    return coachRepository
        .findByIdAndIsDeletedFalse(coachId)
        .orElseThrow(() -> new EntityNotFoundException("Coach not found with id: " + coachId));
  }

  private Driver getActiveDriverById(UUID driverId) {
    return driverRepository
        .findByIdAndIsDeletedFalse(driverId)
        .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId));
  }

  private Destination getDestinationById(UUID destinationId) {
    return destinationRepository
        .findById(destinationId)
        .orElseThrow(
            () -> new EntityNotFoundException("Destination not found with id: " + destinationId));
  }

  private Station getActiveStationById(UUID stationId) {
    return stationRepository
        .findByIdAndIsDeletedFalse(stationId)
        .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + stationId));
  }
}
