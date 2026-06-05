package com.travery.traverybackend.controllers.staff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.booking.BookingMemberResponse;
import com.travery.traverybackend.dtos.response.tour.GuideTourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.security.user.CustomUserDetails;
import com.travery.traverybackend.services.tour.GuideTourInstanceService;
import java.util.List;
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
public class GuideTourInstanceControllerTest {

  private MockMvc mockMvc;

  @Mock private GuideTourInstanceService guideTourInstanceService;

  @Mock private ResponseFactory responseFactory;

  @InjectMocks private GuideTourInstanceController guideTourInstanceController;

  private UUID guideId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(guideTourInstanceController, "responseFactory", responseFactory);
    mockMvc =
        MockMvcBuilders.standaloneSetup(guideTourInstanceController)
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
  void getAssignedInstances_returnsOk() throws Exception {
    TourInstanceResponse response = new TourInstanceResponse();
    List<TourInstanceResponse> instances = List.of(response);

    when(guideTourInstanceService.getAssignedInstances(any(), eq("all"))).thenReturn(instances);

    SingleResponse<List<TourInstanceResponse>> singleResponse = new SingleResponse<>();
    singleResponse.setData(instances);
    singleResponse.setMessage("Fetched assigned tour instances successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(instances), any()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/staff/guide/instances").param("filter", "all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Fetched assigned tour instances successfully"));
  }

  @Test
  void getInstanceDetail_returnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    GuideTourInstanceDetailResponse detail = new GuideTourInstanceDetailResponse();

    when(guideTourInstanceService.getInstanceDetail(any(), eq(id))).thenReturn(detail);

    SingleResponse<GuideTourInstanceDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(detail);
    singleResponse.setMessage("Fetched assigned tour instance detail successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(detail), any())).thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/staff/guide/instances/" + id))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.message").value("Fetched assigned tour instance detail successfully"));
  }

  @Test
  void recordAttendance_returnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    UUID memberId = UUID.randomUUID();
    GuideTourInstanceDetailResponse detail = new GuideTourInstanceDetailResponse();

    when(guideTourInstanceService.recordAttendance(any(), eq(id), any())).thenReturn(detail);

    SingleResponse<GuideTourInstanceDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(detail);
    singleResponse.setMessage("Recorded member attendance successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(detail), any())).thenReturn(ResponseEntity.ok(singleResponse));

    String requestBody =
        "{\"attendances\":[{\"memberId\":\"" + memberId + "\",\"status\":\"PRESENT\"}]}";

    mockMvc
        .perform(
            patch("/api/v1/staff/guide/instances/" + id + "/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Recorded member attendance successfully"));
  }

  @Test
  void searchPassengers_returnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    BookingMemberResponse passenger = new BookingMemberResponse();
    List<BookingMemberResponse> passengers = List.of(passenger);

    when(guideTourInstanceService.searchPassengers(any(), eq(id), eq("John")))
        .thenReturn(passengers);

    SingleResponse<List<BookingMemberResponse>> singleResponse = new SingleResponse<>();
    singleResponse.setData(passengers);
    singleResponse.setMessage("Searched passengers successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(passengers), any()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/staff/guide/instances/" + id + "/passengers").param("query", "John"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Searched passengers successfully"));
  }

  @Test
  void searchPassengers_noQuery_returnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    BookingMemberResponse passenger = new BookingMemberResponse();
    List<BookingMemberResponse> passengers = List.of(passenger);

    when(guideTourInstanceService.searchPassengers(any(), eq(id), eq(null))).thenReturn(passengers);

    SingleResponse<List<BookingMemberResponse>> singleResponse = new SingleResponse<>();
    singleResponse.setData(passengers);
    singleResponse.setMessage("Searched passengers successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(passengers), any()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/staff/guide/instances/" + id + "/passengers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Searched passengers successfully"));
  }

  @Test
  void updateProgress_returnsOk() throws Exception {
    UUID id = UUID.randomUUID();
    GuideTourInstanceDetailResponse response = new GuideTourInstanceDetailResponse();

    when(guideTourInstanceService.updateProgress(any(), eq(id), any())).thenReturn(response);

    SingleResponse<GuideTourInstanceDetailResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(response);
    singleResponse.setMessage("Updated tour progress successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(response), any()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    String requestBody = "{\"status\":\"IN_PROGRESS\"}";

    mockMvc
        .perform(
            patch("/api/v1/staff/guide/instances/" + id + "/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Updated tour progress successfully"));
  }
}
