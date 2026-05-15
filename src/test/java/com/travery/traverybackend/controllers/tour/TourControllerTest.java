package com.travery.traverybackend.controllers.tour;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travery.traverybackend.dtos.request.tour.TourItineraryRequest;
import com.travery.traverybackend.dtos.request.tour.TourTemplateRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.tour.CoordinatorTourInstanceService;
import com.travery.traverybackend.services.tour.TourService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@ExtendWith(MockitoExtension.class)
public class TourControllerTest {

  private MockMvc mockMvc;

  @Mock private TourService tourService;
  @Mock private CoordinatorTourInstanceService coordinatorTourInstanceService;
  @Mock private ResponseFactory responseFactory;
  @InjectMocks private TourController tourController;

  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private UUID coordinatorId = UUID.randomUUID();
  private CustomUserDetails userDetails;

  @BeforeEach
  void setUp() {
    userDetails =
        CustomUserDetails.builder()
            .userId(coordinatorId)
            .email("test@travery.com")
            .isEnabled(true)
            .authorities(List.of())
            .build();

    ReflectionTestUtils.setField(tourController, "responseFactory", responseFactory);

    mockMvc =
        MockMvcBuilders.standaloneSetup(tourController)
            .setCustomArgumentResolvers(
                new HandlerMethodArgumentResolver() {
                  @Override
                  public boolean supportsParameter(
                      org.springframework.core.MethodParameter parameter) {
                    return parameter.hasParameterAnnotation(
                        org.springframework.security.core.annotation.AuthenticationPrincipal.class);
                  }

                  @Override
                  public Object resolveArgument(
                      org.springframework.core.MethodParameter parameter,
                      org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                      org.springframework.web.context.request.NativeWebRequest webRequest,
                      org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                    return userDetails;
                  }
                })
            .build();

    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()));
  }

  @Test
  void createTemplate_returnsCreated() throws Exception {
    TourTemplateRequest request =
        TourTemplateRequest.builder()
            .name("Dalat Trip")
            .destinationId(UUID.randomUUID())
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

    TourResponse response = TourResponse.builder().id(UUID.randomUUID()).name("Dalat Trip").build();

    when(tourService.createTemplate(any(TourTemplateRequest.class), eq(coordinatorId)))
        .thenReturn(response);

    SingleResponse<TourResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(response);
    singleResponse.setMessage("Tour template created successfully");
    singleResponse.setHttpStatus(201);

    when(responseFactory.success(eq(HttpStatus.CREATED), eq(response), any()))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(singleResponse));

    mockMvc
        .perform(
            post("/api/v1/tours/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(new UsernamePasswordAuthenticationToken(userDetails, null)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Tour template created successfully"));
  }
}
