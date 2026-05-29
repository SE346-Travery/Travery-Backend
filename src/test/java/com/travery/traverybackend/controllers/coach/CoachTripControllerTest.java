package com.travery.traverybackend.controllers.coach;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travery.traverybackend.configs.SecurityConfig;
import com.travery.traverybackend.dtos.request.coach.SearchCoachTripRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.dtos.response.coach.SeatMapResponse;
import com.travery.traverybackend.dtos.response.coach.StationResponse;
import com.travery.traverybackend.exception.AppExceptionHandler;
import com.travery.traverybackend.security.jwt.CustomAuthenticationEntryPoint;
import com.travery.traverybackend.security.jwt.JwtService;
import com.travery.traverybackend.services.auth.TokenBlacklistService;
import com.travery.traverybackend.services.coach.CoachTripService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CoachTripController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignore security filters for public endpoints testing
@Import({ResponseFactory.class, SecurityConfig.class, AppExceptionHandler.class})
class CoachTripControllerTest {

  @Autowired private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @MockitoBean private CoachTripService coachTripService;

  @MockitoBean private JwtService jwtService;
  @MockitoBean private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  @MockitoBean private TokenBlacklistService tokenBlacklistService;
  @MockitoBean private DaoAuthenticationProvider daoAuthenticationProvider;
  @MockitoBean private UserDetailsService userDetailsService;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void getStations_ReturnsOk() throws Exception {
    StationResponse resp = new StationResponse();
    resp.setName("Da Lat Station");

    when(coachTripService.getStations()).thenReturn(List.of(resp));

    mockMvc
        .perform(get("/api/v1/stations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Fetched stations successfully"))
        .andExpect(jsonPath("$.data[0].name").value("Da Lat Station"));
  }

  @Test
  void searchTrips_ValidRequest_ReturnsOk() throws Exception {
    SearchCoachTripRequest request =
        SearchCoachTripRequest.builder()
            .originId(UUID.randomUUID())
            .destinationId(UUID.randomUUID())
            .departureDate(LocalDate.now())
            .build();

    CoachTripResponse resp = new CoachTripResponse();
    resp.setTotalSeats(34);

    when(coachTripService.searchTrips(any(SearchCoachTripRequest.class))).thenReturn(List.of(resp));

    mockMvc
        .perform(
            post("/api/v1/coach-trips/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Searched trips successfully"))
        .andExpect(jsonPath("$.data[0].totalSeats").value(34));
  }

  @Test
  void getSeatMap_ValidTrip_ReturnsOk() throws Exception {
    UUID tripId = UUID.randomUUID();
    SeatMapResponse resp = new SeatMapResponse();
    resp.setAvailableSeats(10);

    when(coachTripService.getSeatMap(tripId)).thenReturn(resp);

    mockMvc
        .perform(get("/api/v1/coach-trips/{tripId}/seats", tripId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Fetched seat map successfully"))
        .andExpect(jsonPath("$.data.availableSeats").value(10));
  }
}
