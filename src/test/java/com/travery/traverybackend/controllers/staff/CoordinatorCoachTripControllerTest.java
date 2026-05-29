package com.travery.traverybackend.controllers.staff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travery.traverybackend.dtos.request.coach.CreateCoachTripRequest;
import com.travery.traverybackend.dtos.request.coach.ReassignCoachRequest;
import com.travery.traverybackend.dtos.request.coach.ReassignDriverRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.coach.CoordinatorCoachTripService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class CoordinatorCoachTripControllerTest {

  private MockMvc mockMvc;

  @Mock
  private CoordinatorCoachTripService coordinatorService;

  @Mock
  private ResponseFactory responseFactory;

  @InjectMocks
  private CoordinatorCoachTripController controller;

  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private UUID coordinatorId = UUID.randomUUID();
  private UUID tripId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(controller, "responseFactory", responseFactory);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setCustomArgumentResolvers(
            new PageableHandlerMethodArgumentResolver(),
            new HandlerMethodArgumentResolver() {
              @Override
              public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(
                    org.springframework.security.core.annotation.AuthenticationPrincipal.class);
              }

              @Override
              public Object resolveArgument(
                  MethodParameter parameter,
                  ModelAndViewContainer mavContainer,
                  NativeWebRequest webRequest,
                  WebDataBinderFactory binderFactory) {
                return CustomUserDetails.builder().userId(coordinatorId).build();
              }
            })
        .build();
  }

  @Test
  void createTrip_ReturnsOk() throws Exception {
    CreateCoachTripRequest request = CreateCoachTripRequest.builder()
        .routeId(UUID.randomUUID())
        .coachId(UUID.randomUUID())
        .driverId(UUID.randomUUID())
        .departureTime(LocalDateTime.now().plusDays(1))
        .build();

    CoachTripDetailResponse responseDto = new CoachTripDetailResponse();
    when(coordinatorService.createTrip(any(CreateCoachTripRequest.class), eq(coordinatorId)))
        .thenReturn(responseDto);

    SingleResponse<CoachTripDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(responseDto);
    singleResponse.setMessage("Create coach trip successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(responseDto), anyString()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc.perform(post("/api/v1/coordinator/coach-trips")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Create coach trip successfully"));
  }

  @Test
  void getTrips_ReturnsOk() throws Exception {
    Page<CoachTripResponse> page = new PageImpl<>(List.of(new CoachTripResponse()));
    when(coordinatorService.getTrips(eq(coordinatorId), org.mockito.ArgumentMatchers.nullable(com.travery.traverybackend.enums.coach.CoachTripStatus.class), any())).thenReturn(page);

    SingleResponse<Page<CoachTripResponse>> singleResponse = new SingleResponse<>();
    singleResponse.setData(null); // Avoid PageImpl Jackson serialization issue
    singleResponse.setMessage("Get coach trips successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(page), anyString())).thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc.perform(get("/api/v1/coordinator/coach-trips")
        .param("page", "0")
        .param("size", "10"))
        .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Get coach trips successfully"));
  }

  @Test
  void getTripDetail_ReturnsOk() throws Exception {
    CoachTripDetailResponse responseDto = new CoachTripDetailResponse();
    when(coordinatorService.getTripDetail(tripId)).thenReturn(responseDto);

    SingleResponse<CoachTripDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(responseDto);
    singleResponse.setMessage("Get coach trip detail successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(responseDto), anyString())).thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc.perform(get("/api/v1/coordinator/coach-trips/" + tripId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Get coach trip detail successfully"));
  }

  @Test
  void reassignCoach_ReturnsOk() throws Exception {
    ReassignCoachRequest request = ReassignCoachRequest.builder().coachId(UUID.randomUUID()).build();
    CoachTripDetailResponse responseDto = new CoachTripDetailResponse();

    when(coordinatorService.reassignCoach(eq(tripId), any(UUID.class))).thenReturn(responseDto);

    SingleResponse<CoachTripDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(responseDto);
    singleResponse.setMessage("Reassign coach successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(responseDto), anyString())).thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc.perform(put("/api/v1/coordinator/coach-trips/" + tripId + "/coach")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Reassign coach successfully"));
  }

  @Test
  void reassignDriver_ReturnsOk() throws Exception {
    ReassignDriverRequest request = ReassignDriverRequest.builder().driverId(UUID.randomUUID()).build();
    CoachTripDetailResponse responseDto = new CoachTripDetailResponse();

    when(coordinatorService.reassignDriver(eq(tripId), any(UUID.class))).thenReturn(responseDto);

    SingleResponse<CoachTripDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(responseDto);
    singleResponse.setMessage("Reassign driver successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(responseDto), anyString())).thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc.perform(put("/api/v1/coordinator/coach-trips/" + tripId + "/driver")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Reassign driver successfully"));
  }
}
