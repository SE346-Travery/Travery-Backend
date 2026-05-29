package com.travery.traverybackend.controllers.tour;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
import com.travery.traverybackend.dtos.response.tour.DestinationResponse;
import com.travery.traverybackend.services.tour.DestinationService;
import java.util.List;
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
public class DestinationControllerTest {

  private MockMvc mockMvc;

  @Mock private DestinationService destinationService;

  @Mock private ResponseFactory responseFactory;

  @InjectMocks private DestinationController destinationController;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(destinationController, "responseFactory", responseFactory);
    mockMvc = MockMvcBuilders.standaloneSetup(destinationController).build();
  }

  @Test
  void getAllDestinations_returnsOk() throws Exception {
    List<DestinationResponse> responses =
        List.of(DestinationResponse.builder().name("Da Lat").build());
    when(destinationService.getAllDestinations()).thenReturn(responses);

    SingleResponse<List<DestinationResponse>> singleResponse = new SingleResponse<>();
    singleResponse.setData(responses);
    singleResponse.setMessage("Fetched destinations successfully");
    singleResponse.setHttpStatus(200);

    when(responseFactory.success(eq(responses), any()))
        .thenReturn(ResponseEntity.ok(singleResponse));

    mockMvc
        .perform(get("/api/v1/destinations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Fetched destinations successfully"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0].name").value("Da Lat"));
  }
}
