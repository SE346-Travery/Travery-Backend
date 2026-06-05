package com.travery.traverybackend.controllers.staff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachTripStatusRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.coach.CoachTripDetailResponse;
import com.travery.traverybackend.enums.coach.CoachTripStatus;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.coach.GuideCoachTripService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
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
class GuideCoachTripControllerTest {

  private MockMvc mockMvc;

  @Mock private GuideCoachTripService guideService;

  @Mock private ResponseFactory responseFactory;

  @InjectMocks private GuideCoachTripController controller;

  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private UUID guideId = UUID.randomUUID();
  private UUID tripId = UUID.randomUUID();
  private UUID bookingId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(controller, "responseFactory", responseFactory);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(
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
                    return CustomUserDetails.builder().userId(guideId).build();
                  }
                })
            .build();
  }

  @Test
  void updateTripStatus_ReturnsOk() throws Exception {
    UpdateCoachTripStatusRequest request =
        new UpdateCoachTripStatusRequest(CoachTripStatus.IN_PROGRESS);
    CoachTripDetailResponse responseDto = new CoachTripDetailResponse();

    when(guideService.updateTripStatus(
            eq(guideId), eq(tripId), any(UpdateCoachTripStatusRequest.class)))
        .thenReturn(responseDto);

    SingleResponse<CoachTripDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(responseDto);
    singleResponse.setMessage("Update coach trip status successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(responseDto), anyString()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(
            put("/api/v1/guide/coach-trips/" + tripId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Update coach trip status successfully"));
  }

  @Test
  void markPassengerNoShow_ReturnsOk() throws Exception {
    SuccessResponse successResponse = new SuccessResponse();
    successResponse.setMessage("Mark passenger as no-show successfully");
    successResponse.setHttpStatus(200);

    when(responseFactory.success(any(String.class))).thenReturn(ResponseEntity.ok(successResponse));

    mockMvc
        .perform(put("/api/v1/guide/coach-trips/" + tripId + "/bookings/" + bookingId + "/no-show"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Mark passenger as no-show successfully"));
  }
}
