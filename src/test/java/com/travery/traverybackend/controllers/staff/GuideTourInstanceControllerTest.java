package com.travery.traverybackend.controllers.staff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.travery.traverybackend.dtos.response.ResponseFactory;
import com.travery.traverybackend.dtos.response.base.SingleResponse;
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
}
