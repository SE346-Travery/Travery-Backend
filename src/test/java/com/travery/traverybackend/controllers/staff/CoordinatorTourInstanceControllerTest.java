package com.travery.traverybackend.controllers.staff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceDetailResponse;
import com.travery.traverybackend.dtos.response.tour.TourInstanceResponse;
import com.travery.traverybackend.exception.BaseAppException;
import com.travery.traverybackend.exception.error.WebErrorCode;
import com.travery.traverybackend.services.tour.CoordinatorTourInstanceService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class CoordinatorTourInstanceControllerTest {

  private MockMvc mockMvc;

  @Mock private CoordinatorTourInstanceService coordinatorTourInstanceService;

  @Mock private ResponseFactory responseFactory;

  @InjectMocks private CoordinatorTourInstanceController coordinatorTourInstanceController;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        coordinatorTourInstanceController, "responseFactory", responseFactory);
    mockMvc = MockMvcBuilders.standaloneSetup(coordinatorTourInstanceController).build();
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
}
