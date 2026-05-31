package com.travery.traverybackend.services.coach.impl;

import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.CreateSeatLayoutRequest;
import com.travery.traverybackend.dtos.request.coach.SeatLayoutItemRequest;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.dtos.response.coach.SeatLayoutResponse;
import com.travery.traverybackend.entities.coach.Coach;
import com.travery.traverybackend.entities.coach.SeatLayout;
import com.travery.traverybackend.entities.coach.SeatLayoutItem;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.mappers.CoachMapper;
import com.travery.traverybackend.repositories.coach.CoachRepository;
import com.travery.traverybackend.repositories.coach.SeatLayoutRepository;
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
    return coachMapper.toCoachResponseList(coachRepository.findAll());
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
