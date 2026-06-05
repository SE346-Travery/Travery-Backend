package com.travery.traverybackend.controllers.staff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travery.traverybackend.dtos.request.coach.CreateCoachRequest;
import com.travery.traverybackend.dtos.request.coach.UpdateCoachRequest;
import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.base.SuccessResponse;
import com.travery.traverybackend.dtos.response.coach.CoachResponse;
import com.travery.traverybackend.enums.coach.CoachType;
import com.travery.traverybackend.services.coach.AdminCoachService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminCoachControllerTest {

  private MockMvc mockMvc;

  @Mock private AdminCoachService adminCoachService;

  @Mock private ResponseFactory responseFactory;

  @InjectMocks private AdminCoachController controller;

  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private UUID coachId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(controller, "responseFactory", responseFactory);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void createCoach_ReturnsCreated() throws Exception {
    CreateCoachRequest request =
        CreateCoachRequest.builder()
            .licensePlate("51B-10000")
            .coachType(CoachType.SEAT)
            .capacity(29)
            .seatLayoutId(UUID.randomUUID())
            .build();

    CoachResponse response = new CoachResponse();
    when(adminCoachService.createCoach(any(CreateCoachRequest.class))).thenReturn(response);

    SingleResponse<CoachResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(response);
    singleResponse.setMessage("Coach created successfully");
    singleResponse.setHttpStatus(201);

    when(responseFactory.success(eq(HttpStatus.CREATED), eq(response), anyString()))
        .thenReturn(ResponseEntity.status(201).body(singleResponse));

    mockMvc
        .perform(
            post("/api/v1/admin/coaches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Coach created successfully"));
  }

  @Test
  void getCoaches_ReturnsOk() throws Exception {
    List<CoachResponse> response = List.of(new CoachResponse());
    when(adminCoachService.getCoaches()).thenReturn(response);

    SingleResponse<List<CoachResponse>> singleResponse = new SingleResponse<>();
    singleResponse.setData(response);
    singleResponse.setMessage("Fetched coaches successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(response), anyString()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/admin/coaches"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Fetched coaches successfully"));
  }

  @Test
  void getCoachDetail_ReturnsOk() throws Exception {
    CoachResponse response = new CoachResponse();
    when(adminCoachService.getCoachDetail(coachId)).thenReturn(response);

    SingleResponse<CoachResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(response);
    singleResponse.setMessage("Fetched coach detail successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(response), anyString()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/admin/coaches/" + coachId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Fetched coach detail successfully"));
  }

  @Test
  void updateCoach_ReturnsOk() throws Exception {
    UpdateCoachRequest request =
        UpdateCoachRequest.builder()
            .licensePlate("51B-20000")
            .coachType(CoachType.BED)
            .capacity(34)
            .seatLayoutId(UUID.randomUUID())
            .build();

    CoachResponse response = new CoachResponse();
    when(adminCoachService.updateCoach(eq(coachId), any(UpdateCoachRequest.class)))
        .thenReturn(response);

    SingleResponse<CoachResponse> singleResponse = new SingleResponse<>();
    singleResponse.setData(response);
    singleResponse.setMessage("Coach updated successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(response), anyString()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(
            put("/api/v1/admin/coaches/" + coachId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Coach updated successfully"));
  }

  @Test
  void deleteCoach_ReturnsOk() throws Exception {
    doNothing().when(adminCoachService).deleteCoach(coachId);

    SuccessResponse successResponse = new SuccessResponse();
    successResponse.setMessage("Coach soft-deleted successfully");
    successResponse.setHttpStatus(200);

    when(responseFactory.success(anyString())).thenReturn(ResponseEntity.ok(successResponse));

    mockMvc
        .perform(delete("/api/v1/admin/coaches/" + coachId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Coach soft-deleted successfully"));
  }
}
