package com.travery.traverybackend.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.entities.tour.Tour;
import com.travery.traverybackend.entities.tour.TourInstance;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TourInstanceMapperTest {

  private TourInstanceMapper tourInstanceMapper;

  @BeforeEach
  void setUp() {
    tourInstanceMapper = Mappers.getMapper(TourInstanceMapper.class);
  }

  @Test
  void toTourInstanceResponse_mapsCorrectFields() {
    Tour tour = Tour.builder().name("Da Lat City Tour").maxParticipants(20).build();

    TourInstance instance =
        TourInstance.builder()
            .id(UUID.randomUUID())
            .tour(tour)
            .startDate(LocalDate.now().plusDays(5))
            .endDate(LocalDate.now().plusDays(10))
            .currentParticipants(5)
            .build();

    TourInstanceResponse response = tourInstanceMapper.toTourInstanceResponse(instance);

    assertNotNull(response);
    assertEquals(instance.getId(), response.getId());
    assertEquals("Da Lat City Tour", response.getTourName());
    assertEquals(instance.getStartDate(), response.getStartDate());
    assertEquals(5, response.getCurrentParticipants());
    assertEquals(20, response.getMaxParticipants());
    // endDate and availableSlots should be missing (not mapped/present in DTO)
  }
}
