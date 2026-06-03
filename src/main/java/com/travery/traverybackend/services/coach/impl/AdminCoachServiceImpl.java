package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateDriverRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.SeatLayoutItemRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachStatusRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateDriverRequest;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.DriverResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.Driver;
import com.travery.traverybackend.entities.coach.SeatLayout;
import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import com.travery.traverybackend.enums.coach.CoachStatus;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.enums.coach.DriverStatus;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachRepository;
import com.travery.traverybackend.repositories.coach.DriverRepository;
import com.travery.traverybackend.repositories.coach.SeatLayoutRepository;
import com.travery.traverybackend.services.coach.AdminCoachService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
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
    SeatLayout layout =
        seatLayoutRepository
            .findById(request.getSeatLayoutId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Seat layout not found with id: " + request.getSeatLayoutId()));

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
    return coachMapper.toCoachResponseList(coachRepository.findByStatusNot(CoachStatus.INACTIVE));
  }

  @Override
  @Transactional(readOnly = true)
  public CoachResponse getCoachDetail(UUID coachId) {
    Coach coach =
        coachRepository
            .findById(coachId)
            .orElseThrow(() -> new EntityNotFoundException("Coach not found with id: " + coachId));

    return coachMapper.toCoachResponse(coach);
  }

  @Override
  @Transactional
  public CoachResponse updateCoach(UUID coachId, UpdateCoachRequest request) {
    Coach coach =
        coachRepository
            .findById(coachId)
            .orElseThrow(() -> new EntityNotFoundException("Coach not found with id: " + coachId));

    SeatLayout layout =
        seatLayoutRepository
            .findById(request.getSeatLayoutId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Seat layout not found with id: " + request.getSeatLayoutId()));

    coach.setLicensePlate(request.getLicensePlate());
    coach.setCoachType(request.getCoachType());
    coach.setCapacity(request.getCapacity());
    coach.setSeatLayout(layout);

    Coach updated = coachRepository.save(coach);
    log.info("Updated coach '{}' (id={})", updated.getLicensePlate(), updated.getId());

    return coachMapper.toCoachResponse(updated);
  }

  @Override
  @Transactional
  public CoachResponse updateCoachStatus(UUID coachId, UpdateCoachStatusRequest request) {
    Coach coach =
        coachRepository
            .findById(coachId)
            .orElseThrow(() -> new EntityNotFoundException("Coach not found with id: " + coachId));

    coach.setStatus(request.getStatus());
    Coach updated = coachRepository.save(coach);
    log.info("Updated coach status to '{}' for coach id={}", updated.getStatus(), updated.getId());

    return coachMapper.toCoachResponse(updated);
  }

  @Override
  @Transactional
  public void deleteCoach(UUID coachId) {
    Coach coach =
        coachRepository
            .findById(coachId)
            .orElseThrow(() -> new EntityNotFoundException("Coach not found with id: " + coachId));

    coach.setStatus(CoachStatus.INACTIVE);
    coachRepository.save(coach);
    log.info("Soft-deleted coach id={}", coachId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<DriverResponse> getDrivers() {
    return driverRepository.findByStatusNot(DriverStatus.STOP_WORKING).stream()
        .map(coachMapper::toDriverResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public DriverResponse getDriverDetail(UUID driverId) {
    Driver driver =
        driverRepository
            .findById(driverId)
            .filter(d -> d.getStatus() != DriverStatus.STOP_WORKING)
            .orElseThrow(() -> new EntityNotFoundException("Driver not found or inactive with id: " + driverId));
    return coachMapper.toDriverResponse(driver);
  }

  @Override
  @Transactional
  public DriverResponse createDriver(CreateDriverRequest request) {
    Optional<Driver> existingByPhone = driverRepository.findByPhoneNumber(request.getPhoneNumber());
    Optional<Driver> existingByLicense = driverRepository.findByLicenseNumber(request.getLicenseNumber());

    if (existingByPhone.isPresent() || existingByLicense.isPresent()) {
      Driver existing = existingByPhone.orElseGet(existingByLicense::get);
      
      if (existing.getStatus() == DriverStatus.STOP_WORKING) {
        existing.setStatus(DriverStatus.AVAILABLE);
        existing.setFullName(request.getFullName()); // Update name just in case
        Driver reactivated = driverRepository.save(existing);
        log.info("Reactivated soft-deleted driver '{}' (id={})", reactivated.getFullName(), reactivated.getId());
        return coachMapper.toDriverResponse(reactivated);
      } else {
        throw new com.travery.traverybackend.exception.BaseAppException(
            com.travery.traverybackend.exception.error.WebErrorCode.BAD_REQUEST, 
            "Driver already exists with this phone or license number");
      }
    }

    Driver driver =
        Driver.builder()
            .fullName(request.getFullName())
            .phoneNumber(request.getPhoneNumber())
            .licenseNumber(request.getLicenseNumber())
            .status(DriverStatus.AVAILABLE)
            .build();
    Driver saved = driverRepository.save(driver);
    log.info("Created driver '{}' (id={})", saved.getFullName(), saved.getId());
    return coachMapper.toDriverResponse(saved);
  }

  @Override
  @Transactional
  public DriverResponse updateDriver(UUID driverId, UpdateDriverRequest request) {
    Driver driver =
        driverRepository
            .findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId));

    if (request.getFullName() != null) driver.setFullName(request.getFullName());
    if (request.getPhoneNumber() != null) driver.setPhoneNumber(request.getPhoneNumber());
    if (request.getLicenseNumber() != null) driver.setLicenseNumber(request.getLicenseNumber());
    if (request.getStatus() != null) {
      driver.setStatus(DriverStatus.valueOf(request.getStatus()));
    }

    Driver updated = driverRepository.save(driver);
    log.info("Updated driver '{}' (id={})", updated.getFullName(), updated.getId());
    return coachMapper.toDriverResponse(updated);
  }

  @Override
  @Transactional
  public void deleteDriver(UUID driverId) {
    Driver driver =
        driverRepository
            .findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId));
    driver.setStatus(DriverStatus.STOP_WORKING);
    driverRepository.save(driver);
    log.info("Soft-deleted driver id={}", driverId);
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
}
