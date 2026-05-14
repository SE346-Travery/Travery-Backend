package com.travery.traverybackend.controllers.staff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travery.traverybackend.dtos.request.tour.TourInstanceCreateRequest;
import com.travery.traverybackend.dtos.request.tour.TourInstanceUpdateRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.tour.CoordinatorTourInstanceService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
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
public class CoordinatorTourInstanceControllerTest {

  private MockMvc mockMvc;

  @Mock private CoordinatorTourInstanceService coordinatorTourInstanceService;

  @Mock private ResponseFactory responseFactory;

  @InjectMocks private CoordinatorTourInstanceController coordinatorTourInstanceController;

  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private UUID coordinatorId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        coordinatorTourInstanceController, "responseFactory", responseFactory);
    mockMvc =
        MockMvcBuilders.standaloneSetup(coordinatorTourInstanceController)
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
                    return CustomUserDetails.builder().userId(coordinatorId).build();
                  }
                })
            .build();
  }

  @Test
  void getInstances_returnsOk() throws Exception {
    List<TourInstanceResponse> responses = List.of(new TourInstanceResponse());
    when(coordinatorTourInstanceService.getInstances("all")).thenReturn(responses);

    SingleResponse<List<TourInstanceResponse>> singleResponse = new SingleResponse<>();
    singleResponse.setData(responses);
    singleResponse.setMessage("Fetched tour instances successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(responses), any()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/staff/coordinator/instances").param("filter", "all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Fetched tour instances successfully"))
        .andExpect(jsonPath("$.data").isArray());
  }

  @Test
  void getInstanceDetail_returnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    TourInstanceDetailResponse detailResponse = new TourInstanceDetailResponse();
    when(coordinatorTourInstanceService.getInstanceDetail(id)).thenReturn(detailResponse);

    SingleResponse<TourInstanceDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(detailResponse);
    singleResponse.setMessage("Fetched tour instance detail successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(detailResponse), any()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/staff/coordinator/instances/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Fetched tour instance detail successfully"));
  }

  @Test
  void getInstanceDetail_withInvalidId_throwsException() throws Exception {
    UUID id = UUID.randomUUID();
    when(coordinatorTourInstanceService.getInstanceDetail(id))
        .thenThrow(new BaseAppException(WebErrorCode.BAD_REQUEST, "Tour instance not found"));

    try {
      mockMvc.perform(get("/api/v1/staff/coordinator/instances/" + id));
    } catch (Exception e) {
      assert (e.getCause() instanceof BaseAppException);
      BaseAppException baseException = (BaseAppException) e.getCause();
      assert (baseException.getErrorCode().equals(WebErrorCode.BAD_REQUEST));
    }
  }

  @Test
  void createInstance_returnsCreated() throws Exception {
    TourInstanceCreateRequest request =
        TourInstanceCreateRequest.builder()
            .tourId(UUID.randomUUID())
            .startDate(LocalDate.now().plusDays(10))
            .endDate(LocalDate.now().plusDays(15))
            .build();

    TourInstanceDetailResponse response = new TourInstanceDetailResponse();
    when(coordinatorTourInstanceService.createInstance(any(), eq(coordinatorId)))
        .thenReturn(response);

    SingleResponse<TourInstanceDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(response);
    singleResponse.setMessage("Tour instance created successfully");
    singleResponse.setHttpStatus(201);

    when(responseFactory.success(eq(HttpStatus.CREATED), eq(response), any()))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(singleResponse));

    mockMvc
        .perform(
            post("/api/v1/staff/coordinator/instances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Tour instance created successfully"));
  }

  @Test
  void updateInstance_returnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    TourInstanceUpdateRequest request = new TourInstanceUpdateRequest();
    TourInstanceDetailResponse response = new TourInstanceDetailResponse();

    when(coordinatorTourInstanceService.updateInstance(eq(id), any())).thenReturn(response);

    SingleResponse<TourInstanceDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(response);
    singleResponse.setMessage("Tour instance updated successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(response), any()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(
            patch("/api/v1/staff/coordinator/instances/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Tour instance updated successfully"));
  }
}
